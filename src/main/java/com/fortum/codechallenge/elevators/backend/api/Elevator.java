package com.fortum.codechallenge.elevators.backend.api;

/**
 * Interface for an elevator object.
 */
public interface Elevator {

    /**
     * Tells which status is the current state of elevator
     *
     * @return Status Enumeration value describing the elevator status.
     */
    Status getStatus();

    /**
     * If the elevator is moving. This is the target floor.
     *
     * @return primitive integer number of floor
     */
    int getAddressedFloor();

    /**
     * Get the id of this elevator.
     *
     * @return primitive integer representing the elevator.
     */
    int getId();

    /**
     * Command to move the elevator to the given floor.
     *
     * @param toFloor int where to go.
     */
    void startElevator(int toFloor);

    /**
     * Reports which floor the elevator is at right now.
     *
     * @return int actual floor at the moment.
     */
    int currentFloor();

    /**
     * Tells the elevator to stop on the given floor
     *
     * @param onFloor   number of the floor on which (when passed by) the elevator should stop
     * @param direction direction in which the elevator should be going so that planned stop matches
     *                  to its planned direction
     */
    void planStop(int onFloor, Direction direction);

    /**
     * Used to get current direction of the elevator
     * @return Direction Enumeration value describing current direction.
     */
    Direction getCurrentDirection();

    /**
     * Used to set target floor of the elevator
     * @param toFloor primitive integer - number of the floor the elevator should head toward
     */
    void setAddressedFloor(int toFloor);

}
