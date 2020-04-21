package com.fortum.codechallenge.elevators.backend.resources;

import com.fortum.codechallenge.elevators.backend.api.Direction;
import com.fortum.codechallenge.elevators.backend.api.ElevatorController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.Arrays;

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

    @MockBean
    private SimpMessagingTemplate template;


    @Test
    public void addressElevatorTest() {
        //given
        Mockito.when(elevatorController.chooseDestinationFloorWhenInside(1, 4)).thenReturn(true);
        Mockito.when(elevatorController.chooseDestinationFloorWhenInside(99, 4)).thenReturn(false);

        //when... then
        webClient.post().uri(uriBuilder -> uriBuilder.path("/api/rest/v1/adress-elevator")
                .queryParam("elevatorId", "1")
                .queryParam("floor", "4").build()).exchange().expectStatus().isOk()
                .expectBody(boolean.class).isEqualTo(true);

        webClient.post().uri(uriBuilder -> uriBuilder.path("/api/rest/v1/adress-elevator")
                .queryParam("elevatorId", "99")
                .queryParam("floor", "4").build()).exchange().expectStatus().isOk()
                .expectBody(boolean.class).isEqualTo(false);
    }

    @Test
    public void installElevatorsTest() {
        //given
        Mockito.doNothing().when(elevatorController).installElevators(1, 4);

        //when... then
        webClient.post().uri(uriBuilder -> uriBuilder.path("/api/rest/v1/install")
                .queryParam("numberOfElevators", "1")
                .queryParam("numberOfFloors", "4").build()).exchange().expectStatus().isOk()
                .expectBody().isEmpty();

    }

    @Test
    public void getElevatorsPositionsTest() {

        //given
        Mockito.when(elevatorController.getElevatorsPositions()).thenReturn(new ArrayList<>(Arrays.asList(1, 2, 3)));

        //when... then
        webClient.get().uri(uriBuilder -> uriBuilder.path("/api/rest/v1/positions")
                .build()).exchange().expectStatus().isOk()
                .expectBodyList(Integer.class).hasSize(3);
    }

    @Test
    public void callElevatorTest() {
        //given
        Mockito.when(elevatorController.requestElevator(1, Direction.UP)).thenReturn(2);
        Mockito.when(elevatorController.requestElevator(9, Direction.DOWN)).thenReturn(-1);

        //when... then
        webClient.post().uri(uriBuilder -> uriBuilder.path("/api/rest/v1/call")
                .queryParam("floor", "1")
                .queryParam("direction", "UP").build()).exchange().expectStatus().isOk()
                .expectBody(int.class).isEqualTo(2);

        webClient.post().uri(uriBuilder -> uriBuilder.path("/api/rest/v1/call")
                .queryParam("floor", "9")
                .queryParam("direction", "DOWN").build()).exchange().expectStatus().isOk()
                .expectBody(int.class).isEqualTo(-1);
    }


}
