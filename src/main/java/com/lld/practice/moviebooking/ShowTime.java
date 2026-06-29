package com.lld.practice.moviebooking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ShowTime {
  private String id;
  private String screenLabel; // AUDI1, AUDI2
  private Theater theater;
  private Movie movie;
  private LocalDateTime showTime;

  // CopyOnWriteArrayList: per-seat CAS operations are atomic, but they don't protect this list.
  // Two threads booking completely different seats both reach reservations.add() simultaneously
  // with no mutual exclusion — that's a data race on a plain ArrayList.
  // COWL makes writes safe: every write copies the entire array; reads are lock-free.
  // IMPROVEMENT: Replace with ConcurrentHashMap<String, Reservation> keyed by confirmationId.
  //   - O(1) add/remove/contains vs O(n) for List
  //   - remove() becomes reservations.remove(confirmationId) — no equals()/hashCode() dependency
  //   - Naturally atomic: ConcurrentHashMap.remove() is the atomic gate, eliminates TOCTOU on cancel
  private List<Reservation> reservations;

  // Plain HashMap is correct here: seatMaps is populated once in the constructor and structurally
  // never modified after safe publication. Reads on a safely published immutable map are thread-safe.
  // ConcurrentHashMap would buy nothing — don't pay for synchronisation you don't need.
  // IMPROVEMENT: If seats need to be dynamically added/removed (e.g. blocked seats for VIP rows),
  // switch to ConcurrentHashMap at that point.
  private Map<String, Seat> seatMaps;

  public ShowTime() {
    this.reservations = new CopyOnWriteArrayList<>();
    this.seatMaps = new HashMap<>();
    for (char row = 'A'; row <= 'Z'; ++row) {
      for (int col = 1; col <= 26; ++col) {
        String seatId = "" + row + col;
        seatMaps.put(seatId, new Seat(seatId));
      }
    }
  }

  public String getId() {
    return id;
  }

  public String getScreenLabel() {
    return screenLabel;
  }

  public Theater getTheater() {
    return theater;
  }

  public LocalDateTime getShowTime() {
    return showTime;
  }

  public Movie getMovie() {
    return movie;
  }

  public void book(Reservation reservation) {
    // No lock-ordering needed here (unlike the ReentrantLock approach): CAS threads never block
    // waiting for each other, so two threads cannot form a deadlock cycle regardless of seat order.

    // `acquired` tracks partial CAS wins. If seat N fails after seats 0..N-1 succeeded,
    // acquired holds exactly the set that must be rolled back in the finally block.
    List<String> acquired = new ArrayList<>();
    try {

      for (String seatId : reservation.getSeatIds()) {

        // IMPROVEMENT: Wrap Integer.parseInt in isValidSeatId with try/catch for NumberFormatException —
        // inputs like "A#" or "Axyz" currently crash instead of returning false.
        // Also: column lower bound should be >= 1 (constructor starts at 1, not 0).
        if (!isValidSeatId(seatId)) {
          throw new IllegalStateException("Sorry, Seat is not available.");
        }

        // IMPROVEMENT: The null check below is dead code — if isValidSeatId returns true, the seat
        // was pre-populated in the constructor and can never be null from seatMaps.get(). Remove it.
        Seat seat = seatMaps.get(seatId);
        if (seat == null) {
          throw new IllegalArgumentException("Invalid seatId=%s provided.".formatted(seatId));
        }

        // tryBook() is the single atomic gate: it checks availability AND claims the seat in one CAS.
        // A separate pre-check (isSeatAvailable → tryBook) would create a TOCTOU race — another
        // thread could book the seat in the gap between check and claim.
        if (seat.tryBook()) {
          acquired.add(seatId);
        } else {
          throw new IllegalStateException("Seat can't be booked");
        }
      }

      // All seats CAS'd successfully.
      // IMPROVEMENT: Cleaner rollback signal — call acquired.clear() here instead of the size check.
      // Then the finally block always iterates `acquired` but releases nothing on success (empty list).
      // Eliminates the size comparison entirely and is easier to read.
      if (acquired.size() == reservation.getSeatIds().size()) {
        reservations.add(reservation);
      }

    } finally {
      // Rollback: releases exactly the CAS wins in `acquired` on any failure path.
      // On success, acquired.size() == seatIds.size() so nothing is released.
      // IMPROVEMENT: Replace the size-check idiom with acquired.clear() on the success path (see above).
      if (acquired.size() != reservation.getSeatIds().size()) {
        acquired.stream().map(seatMaps::get).forEach(Seat::free);
      }
    }
  }

  public void cancel(Reservation reservation) {
    // reservations.remove() relies on Reservation.equals() — if equals() is not overridden based
    // on confirmationId, this silently does nothing (reference equality only).
    // IMPROVEMENT: Switch reservations to ConcurrentHashMap<String, Reservation>; then cancel becomes:
    //   Reservation removed = reservations.remove(reservation.getConfirmationId());
    //   if (removed == null) throw new IllegalArgumentException("Reservation not found");
    // This eliminates the equals() dependency entirely and makes the atomic gate explicit.
    if (!reservations.remove(reservation)) {
      throw new IllegalArgumentException("Invalid reservation found");
    }

    reservation.getSeatIds().stream().map(seatMaps::get).forEach(Seat::free);
  }

  // IMPROVEMENT: Implement this — it's requirement #4 in the spec.
  // Return: seatMaps.values().stream().filter(Seat::isAvailable).map(Seat::getId).collect(toList())
  // Wrap in Collections.unmodifiableList() to prevent callers from mutating the result.
  public List<String> getAvailableSeats() {
    return null;
  }

  private boolean isValidSeatId(String seatId) {
    if (seatId == null || seatId.length() < 2) {
      return false;
    }

    char row = seatId.charAt(0);
    // IMPROVEMENT: Wrap in try/catch — Integer.parseInt throws NumberFormatException for inputs
    // like "A#" or "Axyz", crashing the method instead of returning false gracefully.
    // Also: column >= 0 should be >= 1; the constructor populates columns starting at 1.
    int column = Integer.parseInt(seatId.substring(1));

    return row >= 'A' && row <= 'Z' && column >= 0 && column <= 26;
  }
}
