package com.fortum.codechallenge.elevators.backend.api;

/**
 * Interface for an elevator object.
 */
public interface Elevator {

    /**
     * Tells which direction is the elevator going in.
     *
     * @return Direction Enumeration value describing the direction.
     */
    Status getStatus();



    /**
     * If the elevator is moving. This is the target floor.
     *
     * @return primitive integer number of floor
     */
    int getAddressedFloor();

    /**
     * Get the Id of this elevator.
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

    void planStop(int onFloor,Direction direction);

    Direction getCurrentDirection() ;

    void setAddressedFloor(int toFloor);

}
