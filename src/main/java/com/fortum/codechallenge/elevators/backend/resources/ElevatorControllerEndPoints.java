package com.fortum.codechallenge.elevators.backend.resources;

import com.fortum.codechallenge.elevators.backend.api.CallingDirection;
import com.fortum.codechallenge.elevators.backend.api.Direction;
import com.fortum.codechallenge.elevators.backend.api.ElevatorController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Arrays;
import java.util.List;

/**
 * Rest resources used to control system of elevators
 */
@EnableScheduling
@CrossOrigin
@RestController
@RequestMapping("/api/rest/v1")
public final class ElevatorControllerEndPoints {

    @Autowired
    private ElevatorController elevatorController;

    @Autowired
    private SimpMessagingTemplate template;

    /**
     * Service and websocket used to get a snapshot of the current positions of the elevators
     *
     * @return List of Integers - each of list elements indicates position (number of floor) of one elevator -
     * first element = elevator with id =1, second = elevator with id2 etc.
     */
    @Scheduled(fixedRate = 500)
    @MessageMapping("/positions")
    @RequestMapping(value = "/positions", method = RequestMethod.GET)
    public List<Integer> getElevatorsPositions() {
        this.template.convertAndSend("/topic/positions",
                Arrays.toString(elevatorController.getElevatorsPositions().toArray()));
        return elevatorController.getElevatorsPositions();
    }


    /**
     * Service used to call elevator by caller waiting on the floor
     *
     * @param floor number of the floor from which the elevator was called
     * @param direction direction chosen in caller's request
     * @return int - id of the elevator which was chosen to serve the caller's request
     */
    @RequestMapping(value = "/call", method = RequestMethod.POST)
    public int callElevator(@RequestParam(name = "floor") Integer floor, @RequestParam(name = "direction")
            CallingDirection direction) {
        return elevatorController.requestElevator(floor, Direction.getDirection(direction));
    }

    /**
     * Service used to install the elevators in the building
     *
     * @param numberOfElevators number of elevators which should be installed
     * @param numberOfFloors number of floors in the building
     */
    @RequestMapping(value = "/install", method = RequestMethod.POST)
    public void installElevators(
            @RequestParam @Min(value = 1, message = "There should be at least 1 elevator installed")
            @Max(value = 10, message = "Number of elevators cannot exceed 10") int numberOfElevators,
            @RequestParam @Min(value = 1, message = "There should be at least 1 floor in the building")
            @Max(value = 20, message = " Number of floors cannot exceed 20") int numberOfFloors) {
        elevatorController.installElevators(numberOfElevators, numberOfFloors);
    }

    /**
     * Service used when passenger inside the elevator chooses floor number towards which he wants to go
     *
     * @param elevatorId id of the elevator the passenger is in
     * @param floor number of the floor chosen by passenger
     * @return boolean - true if the floor was correctly addressed,
     * false - if the given floor or elevator for the id does not exist
     */
    @RequestMapping(value = "/adress-elevator", method = RequestMethod.POST)
    public boolean addressElevator(@RequestParam int elevatorId, @RequestParam int floor) {
        return elevatorController.chooseDestinationFloorWhenInside(elevatorId, floor);

    }

}
