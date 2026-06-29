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
  private List<Reservation> reservations;
  private Theater theater;
  private Movie movie;
  private LocalDateTime showTime;
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
    List<String> acquired = new ArrayList<>();
    try {

      for (String seatId : reservation.getSeatIds()) {

        if (!isValidSeatId(seatId)) {
          throw new IllegalStateException("Sorry, Seat is not available.");
        }

        Seat seat = seatMaps.get(seatId);
        if (seat == null) {
          throw new IllegalArgumentException("Invalid seatId=%s provided.".formatted(seatId));
        }

        if (seat.tryBook()) {
          acquired.add(seatId);
        } else {
          // throw exceptions
          throw new IllegalStateException("Seat can't be booked");
        }
      }

      if (acquired.size() == reservation.getSeatIds().size()) {
        reservations.add(reservation);
      }

    } finally {
      if (acquired.size() != reservation.getSeatIds().size()) {
        acquired.stream().map(seatMaps::get).forEach(Seat::free);
      }
    }
  }

  public void cancel(Reservation reservation) {

    if (!reservations.remove(reservation)) {
      throw new IllegalArgumentException("Invalid reservation found");
    }

    reservation.getSeatIds().stream().map(seatMaps::get).forEach(Seat::free);
  }

  public List<String> getAvailableSeats() {
    return null;
  }

  private boolean isValidSeatId(String seatId) {

    if (seatId == null || seatId.length() < 2) {
      return false;
    }

    char row = seatId.charAt(0);
    int column = Integer.parseInt(seatId.substring(1));

    return row >= 'A' && row <= 'Z' && column >= 0 && column <= 26;
  }
}
