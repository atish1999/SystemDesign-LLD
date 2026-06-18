package com.lld.practice.moviebooking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookingSystem {
  private List<Theater> theaters;
  private Map<String, Movie> moviesById;
  private Map<String, List<ShowTime>> showTimesByMovieId;

  public BookingSystem() {
    theaters = new ArrayList<>();

    for (Theater theater : theaters) {
      for (ShowTime showTime : theater.getShowTimes()) {
        moviesById.putIfAbsent(showTime.getMovie().getId(), showTime.getMovie());
        showTimesByMovieId
            .computeIfAbsent(showTime.getMovie().getId(), k -> new ArrayList<>())
            .add(showTime);
      }
    }
  }

  public List<ShowTime> searchMovies(String title) {

    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("Valid title is expected");
    }

    List<ShowTime> result = new ArrayList<>();
    LocalDateTime now = LocalDateTime.now();

    for (Movie movie : moviesById.values()) {
      if (!title.equals(movie.getTitle())) {
        continue;
      }

      for (ShowTime showTime : showTimesByMovieId.get(movie.getId())) {
        if (showTime.getShowTime().isBefore(now)) {
          continue;
        }

        result.add(showTime);
      }
    }

    return result;
  }

  public List<ShowTime> searchMoviesByTheater(Theater theater) {
    LocalDateTime now = LocalDateTime.now();
    return theater.getShowTimes().stream()
        .filter(showTime -> !showTime.getShowTime().isBefore(now))
        .toList();
  }

  public void book(ShowTime showTime, List<String> seatIds) {}

  public void cancel(String confirmationId) {}
}
