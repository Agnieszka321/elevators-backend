//package com.fortum.codechallenge.elevators.backend.impl;
//
//import com.fortum.codechallenge.elevators.backend.api.Direction;
//import com.fortum.codechallenge.elevators.backend.api.Elevator;
//import com.fortum.codechallenge.elevators.backend.api.ElevatorController;
//import com.fortum.codechallenge.elevators.backend.api.Status;
//import org.junit.jupiter.api.Test;
//
//public class ElevatorControllerImplTest {
//
//    ElevatorController elevatorController;
//
//    @Test
//    public void testInitialization() throws InterruptedException {
//        this.elevatorController = new ElevatorControllerImpl();
//        elevatorController.installElevators(1,10);
//        elevatorController.getElevators();
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
//    }
//
//}
