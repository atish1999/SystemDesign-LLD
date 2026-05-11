package com.lld.practice.elevator;

import java.util.ArrayList;
import java.util.List;

public class ElevatorController {
  private final List<Elevator> elevators;

  public ElevatorController() {
    this(3);
  }

  public ElevatorController(int elevatorCount) {

    elevators = new ArrayList<>();
    for (int i = 0; i < elevatorCount; ++i) {
      elevators.add(new Elevator());
    }
  }

  public boolean requestFloor(int floorNo, RequestType type) {
    // validation
    if (floorNo < 0 || floorNo > 9) {
      return false;
    }

    if (type == RequestType.DESTINATION) {
      return false;
    }

    Request request = new Request(floorNo, type);
    Elevator elevator = findBestElevator(request);
    if (elevator == null) {
      return false;
    }
    return elevator.addRequest(request);
  }

  private Elevator findBestElevator(Request request) {

    // 1. get the nearest elevator which is moving in the same direction of the request
    Elevator best = findNearestCommittedToFloorElevator(request);
    if (best != null) {
      return best;
    }
    // 2. get the nearest idle elevator
    best = findNearestIdleElevator(request);
    if (best != null) {
      return best;
    }
    // 3. get the nearest elevator
    return findNearestElevator(request);
  }

  private Elevator findNearestCommittedToFloorElevator(Request request) {

    Elevator nearest = null;
    int minDistance = Integer.MAX_VALUE;
    Direction direction = request.getType() == RequestType.PICK_UP ? Direction.UP : Direction.DOWN;

    for (Elevator elevator : elevators) {

      if (elevator.getDirection() != direction) {
        continue;
      }

      // 1. I want to go up but elevator already passed my floor
      // 2. I want to go down but elevator already passed my floor
      if ((direction == Direction.UP && elevator.getCurrentFloor() > request.getFloorNo())
          || (direction == Direction.DOWN && elevator.getCurrentFloor() < request.getFloorNo())) {
        continue;
      }

      // If the elevator does not pass through the floor that we are requesting now, don't assign
      // the elevator for that elevator
      if (!elevator.hasRequestAtOrBeyond(request.getFloorNo(), direction)) {
        continue;
      }

      // we are picking the nearest elevator
      int currentDistance = Math.abs(request.getFloorNo() - elevator.getCurrentFloor());
      if (currentDistance < minDistance) {
        minDistance = currentDistance;
        nearest = elevator;
      }
    }

    return nearest;
  }

  private Elevator findNearestIdleElevator(Request request) {
    Elevator elevator = elevators.getFirst();
    int minDistance = Math.abs(elevator.getCurrentFloor() - request.getFloorNo());

    for (int i = 1; i < elevators.size(); i++) {
      if (elevators.get(i).getDirection() == Direction.IDLE) {
        int currentDistance = Math.abs(elevators.get(i).getCurrentFloor() - request.getFloorNo());
        if (currentDistance < minDistance) {
          minDistance = currentDistance;
          elevator = elevators.get(i);
        }
      }
    }
    return elevator;
  }

  private Elevator findNearestElevator(Request request) {

    Elevator elevator = elevators.getFirst();
    int minDistance = Math.abs(elevator.getCurrentFloor() - request.getFloorNo());

    for (int i = 1; i < elevators.size(); i++) {
      int currentDistance = Math.abs(elevators.get(i).getCurrentFloor() - request.getFloorNo());
      if (currentDistance < minDistance) {
        minDistance = currentDistance;
        elevator = elevators.get(i);
      }
    }
    return elevator;
  }

  public void step() {
    for (Elevator elevator : elevators) {
      elevator.step();
    }
  }
}
