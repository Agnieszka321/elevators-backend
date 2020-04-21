package com.fortum.codechallenge.elevators.backend.impl;

import com.fortum.codechallenge.elevators.backend.api.Direction;
import com.fortum.codechallenge.elevators.backend.api.Elevator;
import com.fortum.codechallenge.elevators.backend.api.Status;

import java.util.ArrayList;
import java.util.Collections;

public class ElevatorImpl implements Elevator, Runnable {

    private int id;

    private Thread elevatorRunningThread;

    private volatile Status status;

    private int addressedFloor;

    private ArrayList<Direction> shouldStop;

    private int currentFloor;

    private Direction currentDirection;

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

    public void setAddressedFloor(int addressedFloor) {
        this.addressedFloor = addressedFloor;
    }

    @Override
    public Direction getCurrentDirection() {
        return currentDirection;
    }

    private void startWaiting() {
        this.status = Status.WAIT;
        this.currentDirection = Direction.NONE;
    }
    public void run() {
        while (this.currentFloor != this.addressedFloor) {
            int movement = (currentDirection == Direction.UP) ? 1 : -1;

            // Controller should not allow for such situation - but in case of broken controller we don't want
            // elevator to break free through the ceiling
            if (currentFloor + movement >= shouldStop.size() || currentFloor + movement < 0) {
                this.startWaiting();
                return;
            }
            currentFloor = currentFloor + movement;
            try {
                if (shouldStop.get(currentFloor) == currentDirection ||
                        shouldStop.get(currentFloor) == Direction.BOTH ||
                        this.addressedFloor == currentFloor) {
                    Thread.sleep(1000);
                    shouldStop.set(currentFloor,
                            shouldStop.get(currentFloor) == Direction.BOTH ?
                                    Direction.opposite(currentDirection) : Direction.NONE);
                } else {
                    Thread.sleep(600);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            if (this.currentFloor == this.addressedFloor && shouldStop.contains(Direction.opposite(currentDirection))) {
                this.addressedFloor = currentDirection == Direction.UP ?
                        shouldStop.indexOf(Direction.DOWN) : shouldStop.lastIndexOf(Direction.UP);
                this.currentDirection = Direction.opposite(this.currentDirection);
            }
        }
        startWaiting();
    }

    private void start() {
        if (elevatorRunningThread == null || !elevatorRunningThread.isAlive()) {
            this.status = Status.RUN;
            elevatorRunningThread = new Thread(this);
            elevatorRunningThread.start();
        }
    }

}
