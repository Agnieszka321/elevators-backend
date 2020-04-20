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

    private Integer numberOfElevators;

    private Integer numberOfFloors;

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
    public Elevator findBestElevator(int callingFloor, Direction chosenDirection) {
        int matchPoints = -numberOfFloors * 3;
        Elevator chosenElevator = elevators.get(0);
        for (Elevator elevator : elevators.values()) {
            if (elevator.getStatus() == Status.OUT_OF_SERVICE) continue;

            int currentPoints = 0;

            if (elevator.getStatus() == Status.WAIT) {
                currentPoints = currentPoints - Math.abs(elevator.currentFloor() - callingFloor);
            } else if (elevator.getCurrentDirection() == chosenDirection) {
                if ((elevator.getCurrentDirection() == Direction.DOWN
                        && elevator.currentFloor() >= callingFloor) ||
                        (elevator.getCurrentDirection() == Direction.UP
                                && elevator.currentFloor() <= callingFloor)) {
                    currentPoints = currentPoints - Math.abs(elevator.currentFloor() - callingFloor) + 5;
                } else if (elevator.getCurrentDirection() == Direction.DOWN) {
                    currentPoints = currentPoints - elevator.currentFloor() - numberOfFloors -
                            (numberOfFloors - callingFloor);
                } else {
                    currentPoints = currentPoints - numberOfFloors - callingFloor -
                            (numberOfFloors - elevator.currentFloor());
                }

            } else if (elevator.getCurrentDirection() == Direction.DOWN) {
                currentPoints = currentPoints - elevator.currentFloor() - callingFloor;
            } else
                currentPoints = currentPoints - (numberOfFloors - elevator.currentFloor())
                        - (numberOfFloors - callingFloor);

            if (currentPoints >= matchPoints) {
                matchPoints = currentPoints;
                chosenElevator = elevator;
            }
        }
        return chosenElevator;
    }

    /**
     * {@inheritDoc}
     *
     * @param numberOfElevators number of elevators which should be installed
     * @param numberOfFloors    number of floors in the building
     */
    @Override
    public void installElevators(int numberOfElevators, int numberOfFloors) {
        this.elevators.clear();
        this.numberOfFloors = numberOfFloors;
        this.numberOfElevators = numberOfElevators;
        for (int i = 0; i < numberOfElevators; i++) {
            int floorNumber = (i % 3 == 2) ? numberOfFloors - 1 : 0;
            this.elevators.put(i, new ElevatorImpl(i, floorNumber, numberOfFloors));
        }
    }

    /**
     * @param elevator
     * @param toFloor
     * @param direction
     */
    public void addressFloor(Elevator elevator, int toFloor, Direction direction) {

        if (elevator.getStatus() == Status.WAIT) {
            elevator.startElevator(toFloor);
        } else if (elevator.getCurrentDirection() == direction) {
            if (elevator.getCurrentDirection() == Direction.DOWN) {
                if (elevator.getAddressedFloor() >= toFloor) {
                    elevator.planStop(elevator.getAddressedFloor(), direction);
                    elevator.setAddressedFloor(toFloor);
                } else {
                    elevator.planStop(toFloor, direction);
                }
            } else if (elevator.getAddressedFloor() <= toFloor) {
                elevator.planStop(elevator.getAddressedFloor(), direction);
                elevator.setAddressedFloor(toFloor);
            } else {
                elevator.planStop(toFloor, direction);
            }
        } else {
            elevator.planStop(toFloor, direction);
        }
    }

    /**
     *
     * @param toFloor addressed floor as integer.
     * @param direction
     * @return
     */
    @Override
    public Elevator requestElevator(int toFloor, Direction direction) {
        Elevator elevator = findBestElevator(toFloor, direction);
        this.addressFloor(elevator, toFloor, direction);
        return elevator;
    }

    /**
     * {@inheritDoc}
     *
     * @param elevatorNumber number of the elevator inside which the request was made
     * @param toFloor        destination floor chosen in the request
     */
    @Override
    public void chooseDestinationFloorWhenInside(int elevatorNumber, int toFloor) {
        Elevator elevator = this.elevators.get(elevatorNumber);
        Direction direction = toFloor < elevator.currentFloor() ? Direction.DOWN : Direction.UP;
        addressFloor(elevator, toFloor, direction);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Elevator> getElevators() {
        return Collections.unmodifiableList(new ArrayList(elevators.values()));
    }

    @Override
    public Integer getNumberOfFloors() {
        return this.numberOfFloors;
    }

    @Override
    public List<Integer> getElevatorsPositions() {
        return elevators.values().stream().map(elevator -> elevator.currentFloor()).collect(Collectors.toList());
    }

    @Override
    public List<Status> getElevatorsStatuses() {
        return this.elevators.values().stream().map(t -> t.getStatus()).collect(Collectors.toList());
    }
}
