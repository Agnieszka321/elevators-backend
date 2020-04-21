package com.fortum.codechallenge.elevators.backend.impl;

import com.fortum.codechallenge.elevators.backend.api.Direction;
import com.fortum.codechallenge.elevators.backend.api.Status;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Used to test {@link ElevatorImpl}
 */
public class ElevatorImplTest {

    @Test
    void createElevatorTest() {

        ElevatorImpl elevator = new ElevatorImpl(1, 5, 15);
        assertThat(elevator.getStatus()).isEqualTo(Status.WAIT);
        assertThat(elevator.currentFloor()).isEqualTo(5);
        assertThat(elevator.getCurrentDirection()).isEqualTo(Direction.NONE);
        assertThat(elevator.getAddressedFloor()).isEqualTo(5);
        assertThat(elevator.getId()).isEqualTo(1);

    }

    @Test
    void elevatorRunTest() {
        ElevatorImpl elevator = new ElevatorImpl(1, 5, 10);
        elevator.startElevator(6);
        assertThat(elevator.getStatus()).isEqualTo(Status.RUN);
        assertThat(elevator.getCurrentDirection()).isEqualTo(Direction.UP);
        while (elevator.getStatus() != Status.WAIT) {
        }
        assertThat(elevator.currentFloor()).isEqualTo(6);
    }

}

