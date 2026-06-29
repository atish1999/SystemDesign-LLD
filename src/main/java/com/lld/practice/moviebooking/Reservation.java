package com.lld.practice.moviebooking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Reservation {
  private String confirmationId;
  private ShowTime showTime;
  private List<String> seatIds;

  // Defensive copy: `new ArrayList<>(seatIds)` instead of `this.seatIds = seatIds`.
  // Why: the caller still holds a reference to the list they passed in. If they mutate it
  // after constructing the Reservation (e.g. add/remove a seat from their booking request),
  // our internal seatIds changes silently — corrupting a confirmed reservation without
  // going through any API. The copy makes Reservation own its data independently of the caller.
  // Rule: if you store a mutable object you didn't create, always copy it at the boundary.
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

  // Unmodifiable view: Collections.unmodifiableList(seatIds) instead of returning seatIds directly.
  // Why: returning the internal list gives the caller a live reference to our private field.
  // They could call getSeatIds().add("Z99") and corrupt the reservation's seat list from outside —
  // no booking or cancel API would be invoked, so seat state and reservation state diverge silently.
  // unmodifiableList wraps it: reads work normally, any mutation attempt throws UnsupportedOperationException.
  // Combined with the defensive copy in the constructor, Reservation becomes effectively immutable
  // after construction — safe to share across threads without synchronisation.
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
