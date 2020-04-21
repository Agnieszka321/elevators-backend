package com.fortum.codechallenge.elevators.backend;

import com.fortum.codechallenge.elevators.backend.api.CallingDirection;
import com.fortum.codechallenge.elevators.backend.api.Status;
import com.fortum.codechallenge.elevators.backend.resources.ElevatorControllerEndPoints;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Boiler plate test class to get up and running with a test faster.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class IntegrationTest {

    @Autowired
    private ElevatorControllerEndPoints endpoints;

    @Test
    @SuppressWarnings("StatementWithEmptyBody")
    public void simulateAnElevatorShaftTest() {

        endpoints.installElevators(4, 10);

        assertThat(endpoints.callElevator(3, CallingDirection.UP)).isEqualTo(3);
        assertThat(endpoints.callElevator(25, CallingDirection.UP)).isEqualTo(-1);
        assertThat(endpoints.callElevator(7, CallingDirection.DOWN)).isEqualTo(2);
        assertThat(endpoints.callElevator(2, CallingDirection.UP)).isEqualTo(3);

        while (endpoints.getElevatorsStatuses().contains(Status.RUN)) {
            // intentionally left empty
        }

        assertThat(endpoints.getElevatorsPositions())
                .isEqualTo(new ArrayList<>(Arrays.asList(0, 0, 7, 3)));

        assertThat(endpoints.addressElevator(1, 7)).isEqualTo(true);
        assertThat(endpoints.addressElevator(1, 6)).isEqualTo(true);
        assertThat(endpoints.addressElevator(1, 100)).isEqualTo(false);
        assertThat(endpoints.callElevator(5, CallingDirection.DOWN)).isEqualTo(3);
        assertThat(endpoints.addressElevator(3, 2)).isEqualTo(true);
        assertThat(endpoints.callElevator(4, CallingDirection.UP)).isEqualTo(3);

        while (endpoints.getElevatorsStatuses().contains(Status.RUN)) {
            // intentionally left empty
        }
        assertThat(endpoints.getElevatorsPositions())
                .isEqualTo(new ArrayList<>(Arrays.asList(0, 7, 7, 2)));

        assertThat(endpoints.callElevator(7, CallingDirection.DOWN)).isEqualTo(2);
        assertThat(endpoints.callElevator(6, CallingDirection.DOWN)).isEqualTo(2);
        assertThat(endpoints.callElevator(1, CallingDirection.UP)).isEqualTo(3);
        assertThat(endpoints.callElevator(2, CallingDirection.UP)).isEqualTo(0);
        assertThat(endpoints.addressElevator(0, 7)).isEqualTo(true);
        assertThat(endpoints.addressElevator(0, 6)).isEqualTo(true);
        assertThat(endpoints.addressElevator(0, 5)).isEqualTo(true);
        assertThat(endpoints.addressElevator(1, 9)).isEqualTo(true);
        assertThat(endpoints.addressElevator(2, 8)).isEqualTo(true);
        assertThat(endpoints.addressElevator(3, 0)).isEqualTo(true);


        while (endpoints.getElevatorsStatuses().contains(Status.RUN)) {
            // intentionally left empty
        }

        assertThat(endpoints.getElevatorsPositions())
                .isEqualTo(new ArrayList<>(Arrays.asList(7, 9, 7, 0)));
    }

}
