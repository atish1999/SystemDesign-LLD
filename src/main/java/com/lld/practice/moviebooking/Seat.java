package com.lld.practice.moviebooking;

import java.util.concurrent.atomic.AtomicReference;

public class Seat {
  private String id;

  // AtomicReference gives lock-free thread safety: no thread ever sleeps waiting for another.
  // IMPROVEMENT: Replace with a DB column (booked BOOLEAN, version INT) for multi-server deployments —
  // JVM-level AtomicReference can't span processes; the DB row becomes the shared CAS target.
  private AtomicReference<SeatState> state;

  // `record` gives structural equals/hashCode automatically — required for compareAndSet to work.
  // The version field prevents the ABA problem: a seat that goes free→booked→free bumps the version,
  // so a stale CAS on an already-freed seat fails rather than silently succeeding.
  // IMPROVEMENT: Add a `heldUntil` timestamp field to model the "seat held for 10 min during checkout"
  // behaviour BookMyShow uses. A background sweeper CAS's held seats back to free on expiry.
  static record SeatState(boolean booked, int version) {}

  public Seat(String id) {
    this.id = id;
    this.state = new AtomicReference<>(new SeatState(false, 0));
  }

  // Optimistic CAS loop: read current state, prepare next state, swap only if unchanged.
  // If another thread raced between our read and CAS, compareAndSet returns false and we retry.
  // IMPROVEMENT: Add a max retry count + random exponential backoff to avoid livelock when two
  // threads contend repeatedly on the same seat (e.g., opening-night rush). TCP does this with
  // collision avoidance — same idea here.
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

  // Same CAS loop as tryBook, used for two callers:
  //   1. ShowTime.book() rollback — partial booking failed, undo CAS wins
  //   2. ShowTime.cancel() — user cancels a confirmed reservation
  // Seat intentionally doesn't know *why* it's being freed; that business logic lives in ShowTime.
  // IMPROVEMENT: Rename to `markAvailable()` to be semantically neutral, and let ShowTime call it
  // from both rollback and cancel paths — makes intent clearer at call sites.
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
