package com.lld.practice.moviebooking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookingSystem {
  private List<Theater> theaters;
  private Map<String, Movie> moviesById;
  private Map<String, List<ShowTime>> showTimesByMovieId;
  private Map<String, ShowTime> showTimesById;
  private Map<String, Reservation> reservationByConfirmationId;

  public BookingSystem() {
    theaters = new ArrayList<>();

    for (Theater theater : theaters) {
      for (ShowTime showTime : theater.getShowTimes()) {
        moviesById.putIfAbsent(showTime.getMovie().getId(), showTime.getMovie());
        showTimesByMovieId
            .computeIfAbsent(showTime.getMovie().getId(), k -> new ArrayList<>())
            .add(showTime);
        showTimesById.put(showTime.getId(), showTime);
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

  public Reservation book(ShowTime showTime, List<String> seatIds) {

    // validation
    if (showTime == null || !showTimesById.containsKey(showTime.getId())) {
      throw new IllegalArgumentException("Invalid ShowTime provided");
    }

    if (seatIds == null || seatIds.isEmpty()) {
      throw new IllegalArgumentException("Invalid seatIds provided");
    }

    // book;
    Reservation reservation = new Reservation(showTime, seatIds);
    showTime.book(reservation);
    reservationByConfirmationId.put(reservation.getConfirmationId(), reservation);
    return reservation;
  }

  public void cancel(String confirmationId) {
    // validation
    if (confirmationId == null || confirmationId.isEmpty()) {
      throw new IllegalArgumentException("Invalid confirmationId provided");
    }

    if (!reservationByConfirmationId.containsKey(confirmationId)) {
      throw new IllegalStateException("Confirmation Id not found");
    }

    // cancel
    Reservation reservation = reservationByConfirmationId.get(confirmationId);
    ShowTime showTime = reservation.getShowTime();
    showTime.cancel(reservation);
  }
}
