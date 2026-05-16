package com.lld.practice.filesystem;

public class NotExistsException extends RuntimeException {
  public NotExistsException(String message) {
    super(message);
  }
}
