package com.fortum.codechallenge.elevators.backend.impl;

import com.fortum.codechallenge.elevators.backend.api.Direction;
import com.fortum.codechallenge.elevators.backend.api.Elevator;
import com.fortum.codechallenge.elevators.backend.api.ElevatorController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test used to test class {@link  ElevatorControllerImpl}
 */

public class ElevatorControllerImplTest {

    @MockBean
    private Elevator elevator;

    @Test
    public void installElevatorsTest() {
        ElevatorController elevatorController = new ElevatorControllerImpl();
        elevatorController.installElevators(3, 10);
        assertThat(elevatorController.getElevators().size()).isEqualTo(3);
        assertThat(elevatorController.getNumberOfFloors()).isEqualTo(10);
        assertThat(elevatorController.getElevatorsPositions().get(0)).isEqualTo(0);
    }

    @Test
    public void requestElevatorTest() {
        ElevatorController elevatorController = new ElevatorControllerImpl();
        elevatorController.installElevators(3, 10);
        assertThat(elevatorController.requestElevator(3, Direction.UP)).isEqualTo(1);
    }

    @Test
    public void chooseDestinationFloorWhenInsideTest() {
        ElevatorController elevatorController = new ElevatorControllerImpl();
        elevatorController.installElevators(3, 10);
        assertThat(elevatorController.chooseDestinationFloorWhenInside(1, 11)).isEqualTo(false);
        assertThat(elevatorController.chooseDestinationFloorWhenInside(99, 3)).isEqualTo(false);
        assertThat(elevatorController.chooseDestinationFloorWhenInside(1, 2)).isEqualTo(true);
    }

}
