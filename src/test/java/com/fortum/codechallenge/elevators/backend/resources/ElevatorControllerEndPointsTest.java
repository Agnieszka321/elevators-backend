package com.fortum.codechallenge.elevators.backend.resources;

import com.fortum.codechallenge.elevators.backend.api.ElevatorController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Boiler plate test class to get up and running with a test faster.
 */
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ElevatorControllerEndPoints.class)
public class ElevatorControllerEndPointsTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ElevatorController elevatorController;

    @Test
    public void ping() {
        webClient.get().uri("/rest/v1/ping").exchange().expectBody(String.class).isEqualTo("pong");
    }
}
