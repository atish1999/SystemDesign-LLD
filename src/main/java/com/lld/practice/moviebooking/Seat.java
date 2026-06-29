package com.lld.practice.moviebooking;

import java.util.concurrent.atomic.AtomicReference;

public class Seat {
  private String id;
  private AtomicReference<SeatState> state;

  static record SeatState(boolean booked, int version) {}

  public Seat(String id) {
    this.id = id;
    this.state = new AtomicReference<>(new SeatState(false, 0));
  }

  public boolean tryBook() {
    while (true) {
      SeatState currentState = state.get();
      if (currentState.booked()) {
        return false;
      }
      SeatState newState = new SeatState(true, currentState.version() + 1);
      if (state.compareAndSet(currentState, newState)) {
        return true;
      }
    }
  }

  public void free() {
    while (true) {
      SeatState currentState = state.get();
      SeatState newState = new SeatState(false, currentState.version + 1);
      if (state.compareAndSet(currentState, newState)) {
        return;
      }
    }
  }

  public boolean isAvailable() {
    return !state.get().booked();
  }

  public String getId() {
    return this.id;
  }
}
