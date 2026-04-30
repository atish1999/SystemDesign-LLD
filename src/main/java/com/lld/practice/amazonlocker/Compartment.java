package com.lld.practice.amazonlocker;

public class Compartment {

  private final Size size;
  private boolean isOccupied;

  public Compartment(Size size) {
    this.size = size;
  }

  public void open() {}

  public void markOccupied() {
    this.isOccupied = true;
  }

  public void markFree() {
    this.isOccupied = false;
  }

  public boolean isAvailable() {
    return !isOccupied;
  }

  public Size getSize() {
    return size;
  }
}
