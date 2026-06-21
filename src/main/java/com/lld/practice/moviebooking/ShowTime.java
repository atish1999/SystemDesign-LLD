package com.lld.practice.moviebooking;

import java.time.LocalDateTime;
import java.util.List;

public class ShowTime {
  private String id;
  private String screenLabel; // AUDI1, AUDI2
  private List<Reservation> reservations;
  private Theater theater;
  private Movie movie;
  private LocalDateTime showTime;

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

    synchronized (this) {
      // check availability
      for (String seatId : reservation.getSeatIds()) {
        if (!isSeatAvailable(seatId)) {
          throw new RuntimeException("Seat is not available for booking");
        }
      }
      // book those seats
      reservations.add(reservation);
    }
  }

  public void cancel(Reservation reservation) {
    synchronized (this) {
      reservations.remove(reservation);
    }
  }

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

    return row >= 'A' && row <= 'Z' && column >= 0 && column <= 26;
  }
}
