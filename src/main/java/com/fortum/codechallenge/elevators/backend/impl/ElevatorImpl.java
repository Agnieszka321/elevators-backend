package com.fortum.codechallenge.elevators.backend.impl;

import com.fortum.codechallenge.elevators.backend.api.Direction;
import com.fortum.codechallenge.elevators.backend.api.Elevator;
import com.fortum.codechallenge.elevators.backend.api.Status;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Implementation of the interface {@link Elevator}
 */
public class ElevatorImpl implements Elevator, Runnable {

    private int id;

    private Thread elevatorRunningThread;

    private volatile Status status;

    private int addressedFloor;

    private ArrayList<Direction> shouldStop;

    private int currentFloor;

    private Direction currentDirection;

    /**
     * Class constructor
     *
     * @param id             id of the elevator
     * @param currentFloor   number of floor on which the elevator should be installed
     * @param numberOfFloors number of floors in the building in which elevator should be installed
     */
    public ElevatorImpl(int id, int currentFloor, int numberOfFloors) {
        this.id = id;
        this.status = Status.WAIT;
        this.currentFloor = currentFloor;
        this.addressedFloor = currentFloor;
        this.currentDirection = Direction.NONE;
        this.shouldStop = new ArrayList<>(Collections.nCopies(numberOfFloors, Direction.NONE));
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    @Override
    public int getAddressedFloor() {
        return addressedFloor;
    }

    public void setAddressedFloor(int addressedFloor) {
        this.planStop(this.addressedFloor, this.currentDirection);
        this.addressedFloor = addressedFloor;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void startElevator(int toFloor) {
        this.addressedFloor = toFloor;
        if (toFloor > this.currentFloor) {
            this.currentDirection = Direction.UP;
        } else {
            this.currentDirection = Direction.DOWN;
        }
        this.start();
    }

    @Override
    public int currentFloor() {
        return currentFloor;
    }

    @Override
    public void planStop(int onFloor, Direction direction) {
        if (shouldStop.get(onFloor) == Direction.NONE) {
            shouldStop.set(onFloor, direction);
        } else if (shouldStop.get(onFloor) != Direction.BOTH) {
            shouldStop.set(onFloor, Direction.BOTH);
        }
    }

    @Override
    public Direction getCurrentDirection() {
        return currentDirection;
    }


    @Override
    public boolean isAhead(int floorNumber) {
        return (isMovingUp() && isLowerThan(floorNumber)) || (isMovingDown() && isHigherThan(floorNumber));
    }

    public void run() {
        while (!hasArrivedToAddressedFloor()) {

            if ((hasReachedTopFloor() && isMovingUp()) || (isMovingDown() && hasReachedLowestFloor())) {
                this.startWaiting();
                return;
            }

            if (isMovingUp()) moveOneFloorUp();
            else moveOneFloorDown();


            try {
                if (shouldStopAtCurrentFloor()) {
                    Thread.sleep(1000);
                    removeCurrentFloorFromPlannedStops();
                } else {
                    Thread.sleep(600);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            if (shouldChangeTheDirection()) {
                changeAddressedFloorChangeDirection();
                changeCurrentDirectionToOpposite();
            }
        }
        startWaiting();
    }

    @Override
    public boolean shouldChangeAddressedFloor(int floorNumber) {
        return
                (isMovingDown() && isAddressedFloorHigherThan(floorNumber))
                        || (isMovingUp() && isAddressedFloorLowerThan(floorNumber));
    }

    private void startWaiting() {
        this.status = Status.WAIT;
        this.currentDirection = Direction.NONE;
    }

    private boolean hasArrivedToAddressedFloor() {
        return this.currentFloor == this.addressedFloor;
    }

    private boolean hasArrivedToPlannedStopFloor() {
        return (shouldStop.get(currentFloor) == currentDirection ||
                shouldStop.get(currentFloor) == Direction.BOTH);
    }

    private boolean isMovingUp() {
        return this.currentDirection == Direction.UP;
    }

    private void moveOneFloorUp() {
        currentFloor = currentFloor + 1;
    }

    private void moveOneFloorDown() {
        currentFloor = currentFloor - 1;
    }

    private boolean shouldStopAtCurrentFloor() {
        return (hasArrivedToPlannedStopFloor() ||
                hasArrivedToAddressedFloor());
    }

    private void removeCurrentFloorFromPlannedStops() {
        shouldStop.set(currentFloor, shouldStopForBothDirections() ?
                Direction.opposite(currentDirection) : Direction.NONE);
    }

    private boolean shouldStopForBothDirections() {
        return shouldStop.get(currentFloor) == Direction.BOTH;
    }

    private boolean hasPlannedStopsInOppositeDirection() {
        return shouldStop.contains(Direction.opposite(currentDirection));
    }

    private boolean shouldChangeTheDirection() {
        return (hasArrivedToAddressedFloor() && hasPlannedStopsInOppositeDirection());
    }

    private boolean hasReachedTopFloor() {
        return currentFloor == shouldStop.size() - 1;
    }

    private boolean hasReachedLowestFloor() {
        return currentFloor == 0;
    }

    private boolean isMovingDown() {
        return this.currentDirection == Direction.DOWN;
    }


    private boolean isLowerThan(int floorNumber) {
        return this.currentFloor < floorNumber;
    }

    private boolean isHigherThan(int floorNumber) {
        return this.currentFloor > floorNumber;
    }

    private void changeAddressedFloorChangeDirection() {
        this.addressedFloor = isMovingUp() ?
                getNumberOfTheLowestPlannedStop() : getNumberOfTheHighestPlannedStop();
    }

    private int getNumberOfTheHighestPlannedStop() {
        return shouldStop.lastIndexOf(Direction.UP);
    }

    private int getNumberOfTheLowestPlannedStop() {
        return shouldStop.indexOf(Direction.DOWN);
    }

    private void changeCurrentDirectionToOpposite() {
        this.currentDirection = Direction.opposite(this.currentDirection);
    }

    private boolean isAddressedFloorHigherThan(int floorNumber) {
        return this.addressedFloor > floorNumber;
    }


    private boolean isAddressedFloorLowerThan(int floorNumber) {
        return this.addressedFloor < floorNumber;
    }


    private void start() {
        if (elevatorRunningThread == null || !elevatorRunningThread.isAlive()) {
            this.status = Status.RUN;
            elevatorRunningThread = new Thread(this);
            elevatorRunningThread.start();
        }
    }

}
