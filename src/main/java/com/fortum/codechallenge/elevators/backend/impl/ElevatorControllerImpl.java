package com.fortum.codechallenge.elevators.backend.impl;

import com.fortum.codechallenge.elevators.backend.api.Direction;
import com.fortum.codechallenge.elevators.backend.api.Elevator;
import com.fortum.codechallenge.elevators.backend.api.ElevatorController;
import com.fortum.codechallenge.elevators.backend.api.Status;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Implementation of the interface {@link ElevatorController}, used to install and manage elevators {@link Elevator}
 * in the building
 */
@Service
public class ElevatorControllerImpl implements ElevatorController {

    private Map<Integer, Elevator> elevators = new HashMap<>();

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
    private Elevator findBestElevator(int callingFloor, Direction chosenDirection) {
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
    private void addressFloor(Elevator elevator, int toFloor, Direction direction) {

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
     *
     * @param elevatorId number of the elevator inside which the request was made
     * @param toFloor        destination floor chosen in the request
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
     * {@inheritDoc}
     */
    @Override
    public List<Elevator> getElevators() {
        return List.copyOf(new ArrayList(elevators.values()));
    }

    @Override
    public int getNumberOfFloors() {
        return this.numberOfFloors;
    }

    @Override
    public List<Integer> getElevatorsPositions() {
        return elevators.values().stream().map(Elevator::currentFloor).collect(Collectors.toList());
    }

}
