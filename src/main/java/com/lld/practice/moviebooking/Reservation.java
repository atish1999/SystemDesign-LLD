package com.lld.practice.moviebooking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Reservation {
  private String confirmationId;
  private ShowTime showTime;
  private List<String> seatIds;

  public Reservation(ShowTime showTime, List<String> seatIds) {
    this.confirmationId = UUID.randomUUID().toString();
    this.showTime = showTime;
    this.seatIds = new ArrayList<>(seatIds);
  }

  public String getConfirmationId() {
    return confirmationId;
  }

  public ShowTime getShowTime() {
    return showTime;
  }

  public List<String> getSeatIds() {
    return Collections.unmodifiableList(seatIds);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (other == null) {
      return false;
    }

    if (!(other instanceof Reservation)) {
      return false;
    }

    return this.getConfirmationId().equals(((Reservation) other).getConfirmationId());
  }

  @Override
  public int hashCode() {
    return this.getConfirmationId().hashCode();
  }
}
