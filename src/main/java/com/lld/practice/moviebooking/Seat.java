package com.lld.practice.moviebooking;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Seat {

  private Lock lock;
  private String id;
  private boolean isAvailable;

  public Seat(String seatId) {
    this.id = seatId;
    this.lock = new ReentrantLock();
  }

  public void acquire() {
    this.lock.lock();
  }

  public void release() {
    this.lock.unlock();
  }

  public String getSeatId() {
    return id;
  }

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
