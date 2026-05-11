package com.lld.practice.elevator;

public class InvalidFloorException extends RuntimeException {
  public InvalidFloorException(String message) {
    super(message);
  }
}
