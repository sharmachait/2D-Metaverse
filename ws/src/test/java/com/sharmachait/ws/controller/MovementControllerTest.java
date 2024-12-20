package com.sharmachait.ws.controller;

import com.sharmachait.ws.models.messages.requestMessages.MovementMessageDto;
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

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MovementControllerTest {
    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private CompletableFuture<MovementMessageDto> movementFuture;

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
                return MovementMessageDto.class;
            }

            @Override
            public void handleFrame(@Nullable StompHeaders headers, Object payload) {
                movementFuture.complete((MovementMessageDto) payload);
            }
        });

        // Create a movement message
        MovementMessageDto sentMessage = new MovementMessageDto();
        sentMessage.setUserId("player1");
        sentMessage.setX(100);
        sentMessage.setY(200);

        // Send the message
        stompSession.send("/app/user/movement", sentMessage);

        // Wait and verify the received message
        MovementMessageDto receivedMessage = movementFuture.get(5, TimeUnit.SECONDS);

        assertEquals(sentMessage.getUserId(), receivedMessage.getUserId());
        assertEquals(sentMessage.getX(), receivedMessage.getX());
        assertEquals(sentMessage.getY(), receivedMessage.getY());
    }

}