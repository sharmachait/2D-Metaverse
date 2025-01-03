package com.sharmachait.ws.controller;

import com.sharmachait.ws.models.messages.MessageType;
import com.sharmachait.ws.models.messages.requestMessages.movement.MovementRequest;
import com.sharmachait.ws.models.messages.requestMessages.movement.MovementRequestPayload;
import jakarta.annotation.Nullable;
import org.springframework.lang.NonNull;
import org.junit.jupiter.api.*;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MovementControllerTest {
    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private CompletableFuture<MovementRequest> movementFuture;

    private String getWsPath() {
        return "ws://localhost:" + port + "/ws";
    }

    @BeforeEach
    public void setup() {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);

        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        movementFuture = new CompletableFuture<>();
    }

    @Test
    public void testSendAndReceiveMovementMessage() throws InterruptedException, ExecutionException, TimeoutException {
        // Establish WebSocket connection
        StompSession stompSession = stompClient
                .connectAsync(getWsPath(), new StompSessionHandlerAdapter() {})
                .get(5, TimeUnit.SECONDS);

        // Subscribe to the movement topic
        stompSession.subscribe("/topic/movement", new StompFrameHandler() {
            @Override
            @NonNull
            public Type getPayloadType(@Nullable StompHeaders headers) {
                return MovementRequest.class;
            }

            @Override
            public void handleFrame(@Nullable StompHeaders headers, Object payload) {
                movementFuture.complete((MovementRequest) payload);
            }
        });

        MovementRequest sentMessage = MovementRequest.builder()
                .type(MessageType.MOVE)
                .payload(MovementRequestPayload.builder()
                        .x(100)
                        .y(200)
                        .token("Bearer eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MzQ3MjE5NjgsImV4cCI6MTczNDgwODM2OCwiYXV0aG9yaXRpZXMiOiJST0xFX0FETUlOIiwiZW1haWwiOiJ3c3NwYWNlY29udHJvbGxlcmFkbWluIiwiaWQiOiI3OGE5NmJhZC02NmQzLTRlYzQtYTU5Yy01YjIzMGE5N2QyYTIifQ.ShA777WRQ_uj58Q4lFEPh4DYVcKwsSnRecZcbu7CuBQ")
                        .build())
                .build();

        // Send the message
        stompSession.send("/app/move", sentMessage);

        // Wait and verify the received message
        MovementRequest receivedMessage = movementFuture.get(5, TimeUnit.SECONDS);

        assertEquals(sentMessage.getPayload().getX(), receivedMessage.getPayload().getX());
        assertEquals(sentMessage.getPayload().getY(), receivedMessage.getPayload().getY());
    }

}