package com.lld.practice.moviebooking;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Seat {

  // One ReentrantLock per seat enables fine-grained concurrency: two threads booking
  // completely different seats hold different locks simultaneously — no unnecessary
  // serialisation, unlike synchronized(this) on ShowTime which would block all bookings.
  // IMPROVEMENT: Replace with ReentrantLock(true) (fair mode) for high-contention seats —
  // fair mode prevents thread starvation on hot seats like A1 on opening night.
  private Lock lock;

  private String id;

  // IMPROVEMENT: Explicitly initialise to true — Java default for boolean is false,
  // so every seat starts as "unavailable" until a booking path touches it, which is wrong.
  // `private boolean isAvailable = true;` makes the intent clear and prevents subtle bugs
  // if construction and first booking race.
  private boolean isAvailable;

  public Seat(String seatId) {
    this.id = seatId;
    this.lock = new ReentrantLock();
  }

  // acquire/release are the external lock handles given to ShowTime.
  // ShowTime controls the full acquire-check-commit-release sequence; Seat only owns the lock object.
  // IMPROVEMENT: Replace lock.lock() with lock.tryLock(timeout, TimeUnit.SECONDS) — an unbounded
  // block lets one slow user's request stall everyone behind them on a popular seat. A timeout
  // lets the system fail fast and tell the user "try again" instead of hanging indefinitely.
  public void acquire() {
    this.lock.lock();
  }

  public void release() {
    this.lock.unlock();
  }

  public String getSeatId() {
    return id;
  }

  // book/cancel change state only while the lock is held by ShowTime — they are intentionally
  // not synchronized themselves. Correctness is guaranteed by the caller holding the lock.
  // IMPROVEMENT: Consolidate book() and cancel() into a single `setState(boolean available)` or
  // rename cancel() to `free()` so the method name is neutral — it is also called during rollback,
  // not only user-facing cancellations. Having two methods with different names for the same
  // operation (set isAvailable = true) is misleading to future readers.
  public void book() {
    this.isAvailable = false;
  }

  public void cancel() {
    this.isAvailable = true;
  }

  public boolean isAvailable() {
    return isAvailable;
  }
}
