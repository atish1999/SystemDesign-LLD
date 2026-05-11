package com.lld.practice.elevator;

import java.util.HashSet;
import java.util.Set;

public class Elevator {
  private int currentFloor;
  private Direction direction;
  // We are having requests here because we want to keep the elevation controller stateless
  Set<Request> requests;

  public Elevator() {
    this.currentFloor = 0;
    this.direction = Direction.IDLE;
    this.requests = new HashSet<>();
  }

  public int getCurrentFloor() {
    return currentFloor;
  }

  public Direction getDirection() {
    return direction;
  }

  // Inside elevator, an user can select for destination
  public boolean addRequest(Request request) {

    // Note: Here also we can add validations on floor [0,9] because inside elevator people can add
    // a destination request
    if (request.getFloorNo() < 0 || request.getFloorNo() > 9) {
      return false;
    }

    // if we are entering the same floor again, and again we are showing as No-op
    if (request.getFloorNo() == currentFloor) {
      return true;
    }

    // if the request is already there in the set it will return false
    // and for the first time it will return true
    return requests.add(request);
  }

  public void step() {
    // if there are no requests no need to move
    if (requests.isEmpty()) {
      direction = Direction.IDLE;
      return;
    }

    // go to the nearest floor
    if (direction == Direction.IDLE) {
      Request nearest = null;
      int minDistance = Integer.MAX_VALUE;

      for (Request request : requests) {
        int currentDistance = Math.abs(currentFloor - request.getFloorNo());
        if (currentDistance < minDistance
            || (currentDistance == minDistance && (request.getFloorNo() < currentFloor))) {
          minDistance = currentDistance;
          nearest = request;
        }
      }

      direction = nearest.getType() == RequestType.PICK_UP ? Direction.UP : Direction.DOWN;
    }

    // whether we need to stop at the current floor or not.
    RequestType pickupType =
        Direction.UP == direction ? RequestType.PICK_UP : RequestType.DESTINATION;
    Request pickupRequest = new Request(currentFloor, pickupType);
    Request destinationRequest = new Request(currentFloor, RequestType.DESTINATION);

    if (requests.contains(pickupRequest) || requests.contains(destinationRequest)) {

      // stopping at a floor means removing the request from the requests
      requests.remove(pickupRequest);
      requests.remove(destinationRequest);

      if (requests.isEmpty()) {
        direction = Direction.IDLE;
        return;
      }
    }

    // do we need to reverse our direction if there are no requests ahead in the direction
    if (!requestsAheadIn(direction)) {
      direction = direction == Direction.UP ? Direction.DOWN : Direction.UP;
      return; // we need to stop first by reversing the direction
    }

    // we have to move in the direction
    if (direction == Direction.UP) {
      currentFloor++;
    } else if (direction == Direction.DOWN) {
      currentFloor--;
    }
  }

  private boolean requestsAheadIn(Direction direction) {

    // this is irrespective of request type whether up , down or destination it just checks
    // if we are going up is there any floor above there we have to go that can be destination or up
    // if we are going down is there any floor below there we have to go that can be destination or
    // down
    for (Request request : requests) {
      if (direction == Direction.UP && request.getFloorNo() > currentFloor) {
        return true;
      }
      if (direction == Direction.DOWN && request.getFloorNo() < currentFloor) {
        return true;
      }
    }

    return false;
  }

  public boolean hasRequestAtOrBeyond(int floorNo, Direction direction) {

    for (Request request : requests) {
      if (direction == Direction.UP && request.getFloorNo() >= floorNo) {
        if (request.getType() == RequestType.PICK_UP
            || request.getType() == RequestType.DESTINATION) {
          return true;
        }
      }

      if (direction == Direction.DOWN && request.getFloorNo() <= floorNo) {

        if (request.getType() == RequestType.PICK_DOWN
            || request.getType() == RequestType.DESTINATION) {
          return true;
        }
      }
    }

    return false;
  }
}
