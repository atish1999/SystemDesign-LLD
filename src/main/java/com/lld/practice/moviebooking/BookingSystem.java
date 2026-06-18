package com.lld.practice.moviebooking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingSystem {
  private List<Theater> theaters;

  public BookingSystem() {
    theaters = new ArrayList<>();
  }

  public List<ShowTime> searchMovies(String title) {

    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("Valid title is expected");
    }

    List<ShowTime> result = new ArrayList<>();
    LocalDateTime now = LocalDateTime.now();

    for (Theater theater : theaters) {
      for (ShowTime showTime : theater.getShowTimes()) {
        if (title.equals(showTime.getMovie().getTitle()) && showTime.getShowTime().isAfter(now)) {
          result.add(showTime);
        }
      }
    }

    return result;
  }

  public List<ShowTime> searchMoviesByTheater(Theater theater) {
    return null;
  }

  public void book(ShowTime showTime, List<String> seatIds) {}

  public void cancel(String confirmationId) {}
}
