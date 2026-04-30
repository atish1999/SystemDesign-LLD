package com.lld.practice.connectfour;

public class InvalidMoveException extends RuntimeException {

  public InvalidMoveException(String message) {
    super(message);
  }
}
