package com.lld.practice.moviebooking;

import java.util.List;
import java.util.UUID;

public class Reservation {
  private String confirmationId;
  private ShowTime showTime;
  private List<String> seatIds;

  public Reservation(ShowTime showTime, List<String> seatIds) {
    this.confirmationId = UUID.randomUUID().toString();
    this.showTime = showTime;
    this.seatIds = seatIds;
  }

  public String getConfirmationId() {
    return confirmationId;
  }

  public ShowTime getShowTime() {
    return showTime;
  }

  public List<String> getSeatIds() {
    return seatIds;
  }

  @Override
  public boolean equals(Object other) {

    if (this == other) return true;
    if (!(other instanceof Reservation)) return false;
    return this.getConfirmationId().equals(((Reservation) other).getConfirmationId());
  }

  @Override
  public int hashCode() {
    return confirmationId.hashCode();
  }
}
