package com.lld.practice.moviebooking;

import java.util.List;
import java.util.UUID;

public class Reservation {
  private String confirmationId;
  private ShowTime showTime;
  private List<String> seatIds;

  // IMPROVEMENT: Use a defensive copy — `this.seatIds = new ArrayList<>(seatIds)` instead of storing
  // the caller's reference directly. The caller still holds their list after handing it to us.
  // If they mutate it (add/remove a seat from their request object), our internal seatIds changes
  // silently — a confirmed reservation now reports different seats without any API call.
  // Rule: if you store a mutable object you didn't create, copy it at the constructor boundary.
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

  // IMPROVEMENT: Return Collections.unmodifiableList(seatIds) instead of the raw internal list.
  // Returning seatIds directly gives the caller a live reference to our private field.
  // They can call getSeatIds().add("Z99") and corrupt the reservation's seat list from outside —
  // no booking or cancel API is invoked, so seat state and reservation state diverge silently.
  // Combined with the defensive copy in the constructor, Reservation becomes effectively immutable
  // after construction — safe to share across threads without any synchronisation.
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
