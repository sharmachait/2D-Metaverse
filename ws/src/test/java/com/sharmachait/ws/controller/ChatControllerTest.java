package com.sharmachait.ws.controller;

import com.sharmachait.ws.config.jwt.JwtProvider;
import com.sharmachait.ws.models.dto.ChatMessageEntityDto;
import com.sharmachait.ws.models.dto.LoginDto;
import com.sharmachait.ws.models.entity.Role;
import com.sharmachait.ws.models.messages.ChatMessage.ChatMessage;
import com.sharmachait.ws.models.messages.ChatMessage.ChatMessagePayload;
import com.sharmachait.ws.models.messages.MessageType;

import com.sharmachait.ws.models.response.AuthResponse;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChatControllerTest {
    @LocalServerPort
    private int port;
    private int httpport = 5455;
    private String userToken;
    private String userId;
    private String adminToken;
    private String adminId;
    private String adminUsername;
    private String userUsername;
    private WebSocketStompClient stompClient;
    private CompletableFuture<ChatMessageEntityDto> chatFuture;
    private String getWsPath() {
        return String.format("ws://localhost:%d/ws", port);
    }
    @BeforeEach
    public void setup() throws Exception {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketTransport webSocketTransport = new WebSocketTransport(webSocketClient);
        List<Transport> transports = Collections.singletonList(webSocketTransport);
        SockJsClient sockJsClient = new SockJsClient(transports);

        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        chatFuture = new CompletableFuture<>();

        RestTemplate restTemplate = new RestTemplate();
        String signupUrl = "http://localhost:" + httpport + "/auth/signup";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("usercontroller");
        loginDto.setPassword("password");
        loginDto.setRole(Role.ROLE_ADMIN);
        HttpEntity<LoginDto> signupRequest = new HttpEntity<>(loginDto, headers);
        ResponseEntity<AuthResponse> signupResponse = restTemplate.postForEntity(signupUrl, signupRequest, AuthResponse.class);

        AuthResponse authResponse = signupResponse.getBody();
        assert authResponse != null : "Signup failed, response is null.";
        userToken = authResponse.getJwt();
        userId = authResponse.getUserId();

        LoginDto adminLoginDto = new LoginDto();
        adminLoginDto.setUsername("admincontroller");
        adminLoginDto.setPassword("password");
        adminLoginDto.setRole(Role.ROLE_ADMIN);
        HttpEntity<LoginDto> signupAdminRequest = new HttpEntity<>(adminLoginDto, headers);
        ResponseEntity<AuthResponse> signupAdminResponse = restTemplate.postForEntity(signupUrl, signupAdminRequest, AuthResponse.class);

        AuthResponse authAdminResponse = signupAdminResponse.getBody();
        assert authAdminResponse != null : "Signup failed, response is null.";
        adminToken = authAdminResponse.getJwt();
        adminId = authAdminResponse.getUserId();
        adminUsername = JwtProvider.getEmailFromToken("Bearer "+adminToken);
        userUsername = JwtProvider.getEmailFromToken("Bearer "+userToken);
    }

    @Test
    public void testSendAndReceiveChatMessage() throws InterruptedException, ExecutionException, TimeoutException {
        StompHeaders connectHeaders = new StompHeaders();
        // Establish WebSocket connection
        StompSession stompSession = stompClient.connect(getWsPath(), new StompSessionHandlerAdapter() {
            @Override
            public void handleException(StompSession session, StompCommand command,
                                        StompHeaders headers, byte[] payload, Throwable exception) {
                chatFuture.completeExceptionally(exception);
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                chatFuture.completeExceptionally(exception);
            }
        }).get(5, TimeUnit.SECONDS);

        StompHeaders subscribeHeaders = new StompHeaders();
        subscribeHeaders.setDestination("/user/" + userUsername + "/queue/messages");
        subscribeHeaders.set("Authorization", "Bearer " + userToken);

        stompSession.subscribe(subscribeHeaders, new StompFrameHandler() {
            @Override
            @NonNull
            public Type getPayloadType(@Nullable StompHeaders headers) {
                return ChatMessageEntityDto.class;
            }

            @Override
            public void handleFrame(@Nullable StompHeaders headers, Object payload) {
                chatFuture.complete((ChatMessageEntityDto) payload);
            }
        });
        Thread.sleep(500);


        ChatMessage sentMessage = ChatMessage.builder()
                .type(MessageType.CHAT)
                .sender(adminUsername)
                .recipient(userUsername)
                .payload(ChatMessagePayload.builder()
                        .token("Bearer "+adminToken)
                        .message("hi")
                        .build())
                .build();

        // Send the message
        StompHeaders sendHeaders = new StompHeaders();
        sendHeaders.setDestination("/app/chat");
        stompSession.send(sendHeaders, sentMessage);


        // Wait and verify the received message
        ChatMessageEntityDto receivedMessage = chatFuture.get(20, TimeUnit.SECONDS);
        ChatMessagePayload sentPayload = sentMessage.getPayload();


        assertNotNull(receivedMessage);
        assertEquals(sentPayload.getMessage(), receivedMessage.getContent());
        assertEquals(sentMessage.getSender(), receivedMessage.getSender());
        assertEquals(sentMessage.getRecipient(), receivedMessage.getRecipient());
        assertNotNull(receivedMessage.getDate());

    }
}