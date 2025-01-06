package com.sharmachait.ws.controller;

import com.sharmachait.ws.models.dto.LoginDto;
import com.sharmachait.ws.models.dto.Role;
import com.sharmachait.ws.models.messages.MessageType;
import com.sharmachait.ws.models.messages.requestMessages.movement.MovementRequest;
import com.sharmachait.ws.models.messages.requestMessages.movement.MovementRequestPayload;
import com.sharmachait.ws.models.response.AuthResponse;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.junit.jupiter.api.*;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.client.WebSocketClient;
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
import java.util.Collections;
import java.util.List;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MovementControllerTest {
    @LocalServerPort
    private int port;
    private int httpport = 5455;
    private String token;
    private String userId;
    private WebSocketStompClient stompClient;
    private CompletableFuture<MovementRequest> movementFuture;

    private String getWsPath() {
        return String.format("ws://localhost:%d/ws", port);
    }

    @BeforeEach
    public void setup() {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketTransport webSocketTransport = new WebSocketTransport(webSocketClient);
        List<Transport> transports = Collections.singletonList(webSocketTransport);
        SockJsClient sockJsClient = new SockJsClient(transports);

        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        movementFuture = new CompletableFuture<>();

        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("usercontroller");
        loginDto.setPassword("password");
        loginDto.setRole(Role.ROLE_ADMIN);
        RestTemplate restTemplate = new RestTemplate();
        String signupUrl = "http://localhost:" + httpport + "/auth/signup";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginDto> signupRequest = new HttpEntity<>(loginDto, headers);
        ResponseEntity<AuthResponse> signupResponse = restTemplate.postForEntity(signupUrl, signupRequest, AuthResponse.class);

        AuthResponse authResponse = signupResponse.getBody();
        assert authResponse != null : "Signup failed, response is null.";
        token = authResponse.getJwt();
        userId = authResponse.getUserId();
    }

    @Test
    public void testSendAndReceiveMovementMessage() throws InterruptedException, ExecutionException, TimeoutException {
        StompHeaders connectHeaders = new StompHeaders();
        // Establish WebSocket connection
        StompSession stompSession = stompClient.connect(getWsPath(), new StompSessionHandlerAdapter() {
            @Override
            public void handleException(StompSession session, StompCommand command,
                                        StompHeaders headers, byte[] payload, Throwable exception) {
                movementFuture.completeExceptionally(exception);
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                movementFuture.completeExceptionally(exception);
            }
        }).get(5, TimeUnit.SECONDS);
        StompHeaders subscribeHeaders = new StompHeaders();
        subscribeHeaders.setDestination("/topic/movement");
        stompSession.subscribe(subscribeHeaders, new StompFrameHandler() {
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
        Thread.sleep(500);
        MovementRequest sentMessage = MovementRequest.builder()
                .type(MessageType.MOVE)
                .payload(MovementRequestPayload.builder()
                        .userId("something")
                        .spaceId("space")
                        .x(100)
                        .y(200)
                        .token("Bearer "+token)
                        .build())
                .build();

        // Send the message
        StompHeaders sendHeaders = new StompHeaders();
        sendHeaders.setDestination("/app/move");
        stompSession.send(sendHeaders, sentMessage);


        // Wait and verify the received message
        MovementRequest receivedMessage = movementFuture.get(5, TimeUnit.SECONDS);
        MovementRequestPayload sentPayload = sentMessage.getPayload();
        MovementRequestPayload receivedPayload =  receivedMessage.getPayload();

        assertNotNull(receivedMessage);
        assertEquals(sentPayload.getX(), receivedPayload.getX());
        assertEquals(sentPayload.getY(), receivedPayload.getY());
    }

}