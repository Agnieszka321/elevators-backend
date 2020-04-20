package com.fortum.codechallenge.elevators.backend.resources;

import com.fortum.codechallenge.elevators.backend.api.Direction;
import com.fortum.codechallenge.elevators.backend.api.Elevator;
import com.fortum.codechallenge.elevators.backend.api.ElevatorController;
import com.fortum.codechallenge.elevators.backend.api.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Rest Resource.
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


    private List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @RequestMapping(value = "/port", method = RequestMethod.GET)
    public SseEmitter elevators() {
        SseEmitter sseEmitter = new SseEmitter();
        emitters.add(sseEmitter);
        return sseEmitter;
    }


    /**
     * Ping service to test if we are alive.
     *
     * @return String pong
     */
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String ping() {
        return "pong";
    }

    /**
     * Service used to get current positions of the elevators
     *
     * @return String pong
     */
    @Scheduled(fixedRate = 500)
    @MessageMapping("/positions")
//    @SendTo("/topic/positions")
//    @RequestMapping(value = "/positions", method = RequestMethod.GET)
    public List<Integer> getElevatorsPositions() {

        this.template.convertAndSend("/topic/positions",
                Arrays.toString(elevatorController.getElevatorsPositions().toArray()));
        if (elevatorController.getElevators().isEmpty()) return Collections.emptyList();
        return elevatorController.getElevatorsPositions();
    }


    /**
     * Service used to get current positions of the elevators
     *
     * @return String pong
     */


    @RequestMapping(value = "/call", method = RequestMethod.POST)
    public int callElevator(@RequestParam(name="floor") Integer floor, @RequestParam(name="direction")
            Direction direction) {
        System.out.println(direction);
        if (elevatorController.getElevators().isEmpty() || floor < 0 || floor > elevatorController.getNumberOfFloors()) {
            return -1;
        } else {
            Elevator elevator = elevatorController.requestElevator(floor, direction);
            return elevator.getId();
        }
    }

    /**
     * Service used to get current positions of the elevators
     *
     * @return String pong
     */
    @RequestMapping(value = "/install", method = RequestMethod.POST)
    public boolean installElevators(
            @RequestParam @Min(value = 1, message = "There should be at least 1 elevator installed")
            @Max(value = 10, message = "Number of elevators cannot exceed 10") int numberOfElevators,
            @RequestParam @Min(value = 1, message = "There should be at least 1 floor in the building")
            @Max(value = 20, message = " Number of floors cannot exceed 20") int numberOfFloors) {
        elevatorController.installElevators(numberOfElevators,numberOfFloors);
        return true;
//        if (elevatorController.getElevators().isEmpty()) {
//            elevatorController.installElevators(numberOfElevators, numberOfFloors + 1);
//            return true;
//        }
//        return false;
    }


    /**
     * Service used to get current positions of the elevators
     *
     * @return String pong
     */

    @RequestMapping(value = "/adressFloor", method = RequestMethod.POST)
    public boolean addressFloor(int elevatorId, int floor) {

        if(elevatorController.getElevators().get(elevatorId) == null){
            return false;
        }
        else{
            elevatorController.chooseDestinationFloorWhenInside(elevatorId, floor);
            return true;
        }
    }


    @RequestMapping(value = "/statuses", method = RequestMethod.GET)
    public List<Status> getElevatorsStatuses() {

//        if(elevatorController.getElevators().get(elevatorId) == null){
//            return true;
//        }

//        else

        return elevatorController.getElevatorsStatuses();

    }


}
