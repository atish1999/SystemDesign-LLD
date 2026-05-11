package com.lld.practice.elevator;

import java.util.Objects;

// value object
public class Request {
  private final int floorNo;
  private final RequestType type;

  public Request(int floorNo, RequestType type) {
    this.floorNo = floorNo;
    this.type = type;
  }

  public int getFloorNo() {
    return floorNo;
  }

  public RequestType getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Request request = (Request) o;
    return floorNo == request.floorNo && type == request.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(floorNo, type);
  }
}
