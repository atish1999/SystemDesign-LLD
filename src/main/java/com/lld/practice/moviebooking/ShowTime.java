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

  public void book(Reservation reservation) {}

  public void cancel(Reservation reservation) {}

  public List<String> getAvailableSeats() {
    return null;
  }

  public boolean isSeatAvailable(String seatId) {
    return false;
  }
}
