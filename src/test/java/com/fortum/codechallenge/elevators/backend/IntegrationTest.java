//package com.fortum.codechallenge.elevators.backend;
//
//import com.fortum.codechallenge.elevators.backend.api.Elevator;
//import com.fortum.codechallenge.elevators.backend.api.Status;
//import com.fortum.codechallenge.elevators.backend.resources.ElevatorControllerEndPoints;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.util.ArrayList;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
///**
// * Boiler plate test class to get up and running with a test faster.
// */
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//public class IntegrationTest {
//
//    @Autowired
//    private ElevatorControllerEndPoints endpoints;
//
//
//    @Test
//    public void simulateAnElevatorShaft() {
//
//        Mockito.whenNew()(elevator..getElevatorsPositions()).thenReturn(new ArrayList<>(Arrays.asList(1, 2, 3)));
//
//        elevatorController.getElevators();
//
//        Elevator elevator = elevatorController.requestElevator(2, Direction.UP);
//        elevatorController.chooseDestinationFloorWhenInside(elevator.getId(),7);
//        Elevator elevator1 = elevatorController.requestElevator(5,Direction.DOWN);
//        Thread.sleep(4000);
//        elevatorController.chooseDestinationFloorWhenInside(elevator.getId(),1);
//
//        while (elevator.getStatus()!= Status.WAIT){
//            System.out.println(elevatorController.getElevatorsPositions());
//            Thread.sleep(2000);
//        }
//        endpoints.installElevators();
//        assertThat(endpoints.ping()).isEqualTo("pong");
//    }
//
//}
