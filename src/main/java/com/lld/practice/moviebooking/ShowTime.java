package com.lld.practice.moviebooking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ShowTime {
  private String id;
  private String screenLabel; // AUDI1, AUDI2
  private List<Reservation> reservations;
  private Theater theater;
  private Movie movie;
  private LocalDateTime showTime;
  private ConcurrentHashMap<String, Seat> seatMaps;

  public ShowTime() {
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

    List<String> acquired = new ArrayList<>();
    List<String> submittedSeatIds = new ArrayList<>(reservation.getSeatIds());
    Collections.sort(submittedSeatIds);
    try {

      for (String seatId : submittedSeatIds) {
        Seat seat = seatMaps.get(seatId);
        if (seat == null) {
          // throw exceptionn
        }

        seat.acquire();
        acquired.add(seatId);
      }

      for (String seatId : submittedSeatIds) {
        if (!seatMaps.get(seatId).isAvailable()) {
          // throw exceptionx
        }
      }

      submittedSeatIds.stream().map(seatMaps::get).forEach(Seat::book);
      reservations.add(reservation);

    } finally {
      acquired.stream().map(seatMaps::get).forEach(Seat::release);
    }
  }

  public void cancel(Reservation reservation) {}

  public List<String> getAvailableSeats() {
    return null;
  }

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
    int column = Integer.parseInt(seatId.substring(1));

    return row >= 'A' && row <= 'Z' && column >= 1 && column <= 20;
  }
}
