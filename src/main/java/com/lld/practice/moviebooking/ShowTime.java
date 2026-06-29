package com.lld.practice.moviebooking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ShowTime {
  private String id;
  private String screenLabel; // AUDI1, AUDI2
  private Theater theater;
  private Movie movie;
  private LocalDateTime showTime;

  // CopyOnWriteArrayList: per-seat locks only protect seat state — not this list.
  // Two threads booking different seats hold different locks, so both can reach
  // reservations.add() simultaneously with no mutual exclusion. That's a data race
  // on a plain ArrayList. COWL makes writes safe: every write copies the array internally.
  // IMPROVEMENT: Replace with ConcurrentHashMap<String, Reservation> keyed by confirmationId.
  //   - O(1) add/remove/contains vs O(n) for CopyOnWriteArrayList
  //   - Eliminates the equals()/hashCode() dependency on Reservation for remove()
  //   - cancel() becomes reservations.remove(confirmationId) — atomic, no TOCTOU risk
  //   - ConcurrentHashMap.remove() is the natural atomic gate for double-free prevention
  private List<Reservation> reservations;

  // ConcurrentHashMap: even though seatMaps is only structurally modified during construction,
  // the map reference is read concurrently by all booking threads. Using ConcurrentHashMap
  // here is a conservative choice — a plain HashMap with safe publication (final field) would
  // also work since the structure never changes after the constructor.
  // IMPROVEMENT: Declare as `private final Map<String, Seat> seatMaps` (plain HashMap) —
  // the final keyword ensures safe publication and ConcurrentHashMap buys nothing extra here.
  private ConcurrentHashMap<String, Seat> seatMaps;

  public ShowTime() {
    this.reservations = new CopyOnWriteArrayList<>();
    this.seatMaps = new ConcurrentHashMap<>();
    for (char row = 'A'; row <= 'Z'; ++row) {
      for (int col = 1; col <= 20; ++col) {
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

    // `acquired` tracks which locks were actually taken. If acquiring the Nth lock throws
    // (NPE for an unknown seatId, InterruptedException), only locks 0..N-1 are held.
    // The finally block releases exactly `acquired` — not the full requested list.
    // Without this, a mid-acquisition failure leaks held locks permanently (deadlock by leak).
    List<String> acquired = new ArrayList<>();

    // Defensive copy: never sort the caller's list in-place. The caller passed ["C3","A1","B2"];
    // sorting it changes their data invisibly — a side effect they didn't ask for.
    // In concurrent systems another thread may be iterating that same list while we sort.
    List<String> submittedSeatIds = new ArrayList<>(reservation.getSeatIds());

    // Phase 1 — acquire all locks in sorted seatId order (global total order).
    // Without ordering: Thread-1 wants [A1,A2], Thread-2 wants [A2,A1]. Each could hold one
    // lock and wait for the other — classic deadlock. Sorted acquisition breaks the cycle.
    // IMPROVEMENT: Deduplicate seatIds before sorting — a request with a repeated seat
    // (e.g. ["A1","A1"]) would try to acquire the same ReentrantLock twice. ReentrantLock is
    // re-entrant so it won't deadlock, but you'd book the seat twice, which is wrong.
    Collections.sort(submittedSeatIds);

    try {

      for (String seatId : submittedSeatIds) {
        Seat seat = seatMaps.get(seatId);
        if (seat == null) {
          // IMPROVEMENT: throw new InvalidSeatException(seatId) — silent no-op means the caller
          // has no idea the seat was invalid; the booking loop then proceeds with the remaining
          // seats and creates a partial reservation. This is a correctness bug.
        }

        seat.acquire();
        acquired.add(seatId);
      }

      // Phase 2 — check availability AFTER holding all locks (TOCTOU prevention).
      // Checking before acquiring locks would be a race: another thread could book a seat
      // between our check and our lock acquisition. Here the seat state cannot change while
      // we hold its lock — the check is inside the critical section.
      for (String seatId : submittedSeatIds) {
        if (!seatMaps.get(seatId).isAvailable()) {
          // IMPROVEMENT: throw new SeatNotAvailableException(seatId) — silent no-op skips
          // the unavailable seat and still commits the rest of the seats, creating a partial
          // booking with no signal to the caller. Must throw to trigger the finally rollback.
        }
      }

      // Phase 3 — commit: all seats verified free while locks are held, so this is atomic.
      // No other thread can interleave here because we hold every seat's lock.
      submittedSeatIds.stream().map(seatMaps::get).forEach(Seat::book);
      reservations.add(reservation);

    } finally {
      // Always release every lock in `acquired` (not submittedSeatIds).
      // ReentrantLock does NOT auto-release on exception unlike synchronized blocks —
      // failing to release here permanently deadlocks any future thread requesting these seats.
      // IMPROVEMENT: Consider whether locks should always be released in finally even on success —
      // they should, because the seats are now committed (Seat.book() set isAvailable=false).
      // Holding locks beyond the commit point only blocks reads unnecessarily.
      acquired.stream().map(seatMaps::get).forEach(Seat::release);
    }
  }

  public void cancel(Reservation reservation) {
    List<String> acquired = new ArrayList<>();

    // Defensive copy + sort: same reasoning as book() — don't mutate caller's list,
    // and acquire locks in sorted order to maintain global lock ordering.
    // A cancel and a concurrent book for the same seats must agree on lock order,
    // or they can deadlock each other.
    List<String> booked = new ArrayList<>(reservation.getSeatIds());
    Collections.sort(booked);

    try {

      for (String seatId : booked) {
        Seat seat = seatMaps.get(seatId);
        if (seat == null) {
          // IMPROVEMENT: throw exception — same silent-no-op problem as in book().
        }
        seat.acquire();
        acquired.add(seatId);
      }

      // IMPROVEMENT: Validate reservation exists BEFORE releasing seats.
      // Current order: release seats first, then remove reservation.
      // If an invalid confirmationId is passed, seats get freed before we even check whether
      // the reservation is real — corrupting state for a non-existent booking.
      // Correct order: check reservations.contains(reservation) first, throw if absent,
      // then proceed with seat cancellation.
      for (String seatId : booked) {
        Seat seat = seatMaps.get(seatId);
        seat.cancel();
      }

      // IMPROVEMENT: reservations.remove(reservation) relies on Reservation.equals() — without
      // overriding equals() based on confirmationId, this silently does nothing (reference equality).
      // Also: contains() + remove() on CopyOnWriteArrayList are individually atomic but NOT together.
      // Two threads cancelling the same reservation both pass contains(), both call remove(), both
      // free all seats — seats get freed twice (double-free). Use remove() as the single atomic gate:
      //   if (!reservations.remove(reservation)) throw new IllegalArgumentException("Not found");
      reservations.remove(reservation);

    } finally {
      acquired.stream().map(seatMaps::get).forEach(Seat::release);
    }
  }

  // IMPROVEMENT: Implement this — it's requirement #4 in the spec.
  // Return: seatMaps.values().stream().filter(Seat::isAvailable).map(Seat::getSeatId).collect(toList())
  // Wrap in Collections.unmodifiableList() to prevent callers from mutating the result.
  public List<String> getAvailableSeats() {
    return null;
  }

  // IMPROVEMENT: isSeatAvailable scans the reservations list — this is the wrong source of truth.
  // Seat.isAvailable() already tracks availability; scanning reservations is O(n×m), redundant,
  // and can diverge from Seat state if a booking path updates one but not the other.
  // Replace the scan with: return seatMaps.get(seatId).isAvailable()
  // Keep this method as the public API but fix the implementation behind it.
  public boolean isSeatAvailable(String seatId) {

    if (!isValidSeatId(seatId)) {
      throw new IllegalArgumentException("Invalid seatId=%s provided ".formatted(seatId));
    }

    boolean isAvailable = true;
    for (Reservation reservation : reservations) {
      if (reservation.getSeatIds().contains(seatId)) {
        isAvailable = false;
        break;
      }
    }

    return isAvailable;
  }

  private boolean isValidSeatId(String seatId) {

    if (seatId == null || seatId.length() < 2) {
      return false;
    }

    char row = seatId.charAt(0);
    // IMPROVEMENT: Wrap Integer.parseInt in try/catch NumberFormatException — inputs like "A#"
    // or "Axyz" crash here instead of returning false. The method should never throw; it should
    // return false for any malformed input.
    int column = Integer.parseInt(seatId.substring(1));

    return row >= 'A' && row <= 'Z' && column >= 1 && column <= 20;
  }
}
