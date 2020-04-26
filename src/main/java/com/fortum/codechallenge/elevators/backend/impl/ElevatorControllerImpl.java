package com.fortum.codechallenge.elevators.backend.impl;

import com.fortum.codechallenge.elevators.backend.api.Direction;
import com.fortum.codechallenge.elevators.backend.api.Elevator;
import com.fortum.codechallenge.elevators.backend.api.ElevatorController;
import com.fortum.codechallenge.elevators.backend.api.Status;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Implementation of the interface {@link ElevatorController}, used to install and manage elevators {@link Elevator}
 * in the building
 */
@Service
public class ElevatorControllerImpl implements ElevatorController {

    private Map<Integer, Elevator> elevators = new HashMap<>();

    private int numberOfFloors;


    /**
     * {@inheritDoc}
     * <p>
     * Every third elevator will go to the top of the building to wait for passengers there
     *
     * @param numberOfElevators number of elevators which should be installed
     * @param numberOfFloors    number of floors in the building
     */
    @Override
    public void installElevators(int numberOfElevators, int numberOfFloors) {
        this.elevators.clear();
        this.numberOfFloors = numberOfFloors;
        for (int i = 0; i < numberOfElevators; i++) {
            int floorNumber = (i % 3 == 2) ? numberOfFloors - 1 : 0;
            this.elevators.put(i, new ElevatorImpl(i, floorNumber, numberOfFloors));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param elevatorId number of the elevator inside which the request was made
     * @param toFloor    destination floor chosen in the request
     * @return boolean indicating whether elevator for given id was found and target floor was properly addressed
     */
    @Override
    public boolean chooseDestinationFloorWhenInside(int elevatorId, int toFloor) {
        Elevator elevator = this.elevators.get(elevatorId);
        if (elevator == null || toFloor > this.numberOfFloors) {
            return false;
        }
        Direction direction = toFloor < elevator.currentFloor() ? Direction.DOWN : Direction.UP;
        addressFloor(elevator, toFloor, direction);
        return true;

    }

    /**
     * @param toFloor   addressed floor as integer.
     * @param direction chosen direction of the elevator
     * @return integer - id of the elevator which will serve the request
     */
    @Override
    public int requestElevator(int toFloor, Direction direction) {
        if (toFloor > numberOfFloors || this.elevators.isEmpty()) return -1;
        Elevator elevator = findBestElevator(toFloor, direction);
        this.addressFloor(elevator, toFloor, direction);
        return elevator.getId();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Elevator> getElevators() {
        return Collections.unmodifiableList(new ArrayList(elevators.values()));
    }

    @Override
    public int getNumberOfFloors() {
        return this.numberOfFloors;
    }

    @Override
    public List<Integer> getElevatorsPositions() {
        return elevators.values().stream().map(Elevator::currentFloor).collect(Collectors.toList());
    }

    @Override
    public List<Status> getElevatorStatuses() {
        return elevators.values().stream().map(Elevator::getStatus).collect(Collectors.toList());
    }


    /**
     * Method used to address target floor of the elevator
     *
     * @param elevator  elevator which target field will be addressed
     * @param toFloor   number of the target floor
     * @param direction direction  which the elevator should be moving in
     */
    private void addressFloor(Elevator elevator, int toFloor, Direction direction) {

        if (elevator.getStatus() == Status.WAIT) {
            elevator.startElevator(toFloor);
            return;
        }
        if (isElevatorMovingInTheSameDirection(elevator.getCurrentDirection(), direction)
                && elevator.shouldChangeAddressedFloor(toFloor)) {
            elevator.setAddressedFloor(toFloor);
            return;
        }

        elevator.planStop(toFloor, direction);

    }

    /**
     * Method for finding elevator which shall serve the request in the most effective way
     * Score of the elevator will be the best if:
     * directions of the moving elevator and the one chosen by passenger are the same
     * elevator is yet to come
     * the closer elevator is the higher score it will get
     *
     * @param callingFloor    - number of the floor from which the elevator request was made
     * @param chosenDirection - direction chosen in the request
     * @return elevator which best matches the request
     */
    private Elevator findBestElevator(int callingFloor, Direction chosenDirection) {
        int bestScore = -numberOfFloors * 3;
        Elevator chosenElevator = elevators.get(0);
        for (Elevator elevator : elevators.values()) {
            if (elevator.getStatus() == Status.OUT_OF_SERVICE) continue;

            int currentScore = calculateElevatorScore(elevator, callingFloor, chosenDirection);

            if (currentScore >= bestScore) {
                bestScore = currentScore;
                chosenElevator = elevator;
            }
        }
        return chosenElevator;
    }

    private int calculateElevatorScore(Elevator elevator, int callingFloor, Direction chosenDirection) {
        Direction elevatorDirection = elevator.getCurrentDirection();
        int elevatorFloor = elevator.currentFloor();

        if (elevator.getStatus() == Status.WAIT) {
            return scoreForWaitingElevator(elevatorFloor, callingFloor);
        }
        if (isElevatorMovingInTheSameDirection(elevatorDirection, chosenDirection)) {
            if (elevator.isAhead(callingFloor)) return scoreForSameDirectionElevatorAhead(elevatorFloor, callingFloor);
            if (isMovingDown(elevatorDirection))
                return scoreForSameDirectionElevatorPassedMovingDown(elevatorFloor, callingFloor);
            return scoreForSameDirectionElevatorPassedMovingUp(elevatorFloor, callingFloor);
        }
        if (isMovingDown(elevatorDirection)) return scoreForDifferentDirectionMovingDown(elevatorFloor, callingFloor);
        return scoreForDifferentDirectionMovingUp(elevatorFloor, callingFloor);
    }

    private int scoreForSameDirectionElevatorAhead(int elevatorFloor, int callingFloor) {
        return -distanceBetweenFloors(elevatorFloor, callingFloor) + 5;
    }

    private int scoreForWaitingElevator(int elevatorFloor, int callingFloor) {
        return -distanceBetweenFloors(elevatorFloor, callingFloor);

    }

    private int scoreForSameDirectionElevatorPassedMovingDown(int elevatorFloor, int callingFloor) {
        return -elevatorFloor - numberOfFloors - distanceFromHighestFloor(callingFloor);
    }

    private int scoreForDifferentDirectionMovingDown(int elevatorFloor, int callingFloor) {
        return -elevatorFloor - callingFloor;
    }

    private int scoreForDifferentDirectionMovingUp(int elevatorFloor, int callingFloor) {
        return -distanceFromHighestFloor(elevatorFloor) - distanceFromHighestFloor(callingFloor);
    }

    private int scoreForSameDirectionElevatorPassedMovingUp(int elevatorFloor, int callingFloor) {
        return -numberOfFloors - callingFloor - distanceFromHighestFloor(elevatorFloor);
    }

    private boolean isElevatorMovingInTheSameDirection(Direction elevatorDirection, Direction chosenDirection) {
        return elevatorDirection == chosenDirection;
    }

    private boolean isMovingDown(Direction direction) {
        return direction == Direction.DOWN;
    }

    private int distanceBetweenFloors(int currentFloor, int callingFloor) {
        return Math.abs(currentFloor - callingFloor);
    }

    private int distanceFromHighestFloor(int callingFloor) {
        return Math.abs(numberOfFloors - callingFloor);
    }


}
