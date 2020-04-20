package com.fortum.codechallenge.elevators.backend.api;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Interface for the Elevator Controller.
 */
public interface ElevatorController {

    /**
     * Request an elevator to the specified floor.
     *
     * @param toFloor addressed floor as integer.
     * @param direction chosen direction in which the passenger wants to move
     * @return The Elevator that is going to the floor, if there is one to move.
     */
    Elevator requestElevator(int toFloor, Direction direction);

    /**
     * A snapshot list of all elevators in the system.
     *
     * @return A List with all {@link Elevator} objects.
     */
    List<Elevator> getElevators();

    /**
     * Method used to choose destination floor from inside of the elevator
     * @param elevatorNumber number of the elevator inside which the request was made
     * @param toFloor destination floor chosen
     */
    void chooseDestinationFloorWhenInside(int elevatorNumber, int toFloor);


    /**
     * Method used to install elevators in the building
     *
     * @param numberOfElevators number of elevators which should be installed
     * @param numberOfFloors number of floors in the building
     */
    void installElevators(int numberOfElevators, int numberOfFloors);

    /**
     * A snapshot list of all elevators' positions in the system.
     * @return
     */
    List<Integer> getElevatorsPositions();

    List<Status> getElevatorsStatuses();

    Integer getNumberOfFloors();
}
