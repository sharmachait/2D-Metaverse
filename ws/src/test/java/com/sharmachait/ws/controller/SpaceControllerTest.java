package com.sharmachait.ws.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharmachait.ws.models.dto.*;
import com.sharmachait.ws.models.entity.Role;
import com.sharmachait.ws.models.messages.requestMessages.joinSpace.JoinSpaceRequest;
import com.sharmachait.ws.models.messages.requestMessages.joinSpace.JoinSpaceRequestPayload;
import com.sharmachait.ws.models.messages.MessageType;
import com.sharmachait.ws.models.messages.requestMessages.movement.MovementRequest;
import com.sharmachait.ws.models.messages.requestMessages.movement.MovementRequestPayload;
import com.sharmachait.ws.models.messages.responseMessages.joinedSpace.JoinSpaceResponse;
import com.sharmachait.ws.models.messages.responseMessages.joinedSpace.UserSpawn;
import com.sharmachait.ws.models.messages.responseMessages.leaveSpace.LeaveSpaceResponse;
import com.sharmachait.ws.models.messages.responseMessages.movement.MovementResponse;
import com.sharmachait.ws.models.response.AuthResponse;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
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
class SpaceControllerTest {
    @Value("${local.server.port}")
    void setServerPort(int serverPort) {
        SpaceControllerTest.serverPort = serverPort;
    }

    private static int serverPort;
    private static int apiPort = 5455;
    private static String adminToken;
    private static String userToken;
    private static String adminId;
    private static String element1Id;
    private static String element2Id;
    private static String userId;
    private static String mapId;
    private static String spaceId;
    private static RestTemplate restTemplate;
    private static HttpHeaders headers = new HttpHeaders();
    private static int userX, userY, adminX, adminY;


    @BeforeAll
    static void setUp() throws ExecutionException, InterruptedException, TimeoutException {
        setUpHttp();
        setUpWs();
    }

    static void setUpHttp(){
        // Step 1: Signup as admin
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("wsspacecontrolleradmin");
        loginDto.setPassword("password");
        loginDto.setRole(Role.ROLE_ADMIN);
        restTemplate = new RestTemplate();
        String signupUrl = "http://localhost:" + apiPort + "/auth/signup";

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginDto> signupRequest = new HttpEntity<>(loginDto, headers);
        ResponseEntity<AuthResponse> signupResponse = restTemplate.postForEntity(signupUrl, signupRequest, AuthResponse.class);
        AuthResponse authResponse = signupResponse.getBody();
        assert authResponse != null : "Signup failed, response is null.";
        adminToken = authResponse.getJwt();
        adminId = authResponse.getUserId();

        // Step 1: Signup as User
        loginDto.setUsername("wsspacecontrolleruser");
        loginDto.setRole(Role.ROLE_USER);
        HttpEntity<LoginDto> signupRequestUser = new HttpEntity<>(loginDto, headers);
        ResponseEntity<AuthResponse> signupResponseUser = restTemplate.postForEntity(signupUrl, signupRequestUser, AuthResponse.class);
        AuthResponse authResponseUser = signupResponseUser.getBody();
        assert authResponseUser != null : "Signup failed, response is null.";
        userToken = authResponseUser.getJwt();
        userId = authResponseUser.getUserId();

        // Step 2: Create Elements
        String elementUrl = "http://localhost:" + apiPort + "/api/v1/admin/element";
        headers.add("Authorization", "Bearer " + adminToken);
        ElementDto elementDto = new ElementDto();
        elementDto.setImageUrl("https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcRCRca3wAR4zjPPTzeIY9rSwbbqB6bB2hVkoTXN4eerXOIkJTG1GpZ9ZqSGYafQPToWy_JTcmV5RHXsAsWQC3tKnMlH_CsibsSZ5oJtbakq&usqp=CAE");
        elementDto.setWidth(1);
        elementDto.setHeight(1);
        elementDto.setIsStatic(true);
        HttpEntity<ElementDto> elementRequest = new HttpEntity<>(elementDto, headers);
        ResponseEntity<ElementDto> elementResponse = restTemplate.postForEntity(elementUrl, elementRequest, ElementDto.class);
        ResponseEntity<ElementDto> elementResponse2 = restTemplate.postForEntity(elementUrl, elementRequest, ElementDto.class);
        element1Id = elementResponse.getBody().getId();
        element2Id = elementResponse2.getBody().getId();

        // Step 3: Create Map
        String mapUrl = "http://localhost:" + apiPort + "/api/v1/admin/map";
        GameMapDto mapDto = new GameMapDto();
        mapDto.setThumbnail("https://thumbnail.com/a.png");
        mapDto.setDimensions("100x200");
        MapElementDto mapElement1 = MapElementDto.builder()
                .elementId(element1Id)
                .x(20)
                .y(20)
                .build();
        MapElementDto mapElement2 = MapElementDto.builder()
                .elementId(element1Id)
                .x(18)
                .y(20)
                .build();
        MapElementDto mapElement3 = MapElementDto.builder()
                .elementId(element2Id)
                .x(19)
                .y(20)
                .build();
        MapElementDto mapElement4 = MapElementDto.builder()
                .elementId(element2Id)
                .x(19)
                .y(20)
                .build();
        List<MapElementDto> l = List.of(mapElement1, mapElement2, mapElement3, mapElement4);
        mapDto.setMapElements(l);
        HttpEntity<GameMapDto> mapRequest = new HttpEntity<>(mapDto, headers);
        ResponseEntity<GameMapDto> mapResponse = restTemplate.postForEntity(mapUrl, mapRequest, GameMapDto.class);
        mapId = mapResponse.getBody().getId();

        //step 4: create a Space
        String spaceUrl = "http://localhost:" + apiPort + "/api/v1/space";
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + userToken);
        SpaceDto spaceDto = SpaceDto.builder()
                .name("Test Space")
                .dimensions("100x200")
                .mapId(mapId)
                .build();
        HttpEntity<SpaceDto> request = new HttpEntity<>(spaceDto, headers);
        ResponseEntity<SpaceDto> spaceResponse = restTemplate.postForEntity(spaceUrl, request, SpaceDto.class);
        spaceId = spaceResponse.getBody().getId();
    }

    private static String getWsPath() {
        return "ws://localhost:" + serverPort + "/ws";
    }

    private static WebSocketStompClient stompClient;
    private static StompSession ws1,ws2;
    private static List<Object> ws1Messages, ws2Messages;

    static void setUpWs() throws ExecutionException, InterruptedException, TimeoutException {

        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);

        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        ws1Messages = new ArrayList<>();
        ws2Messages = new ArrayList<>();

        ws1 = stompClient.connectAsync(getWsPath(), new StompSessionHandlerAdapter() {})
                .get(5, TimeUnit.SECONDS);
        ws2 = stompClient.connectAsync(getWsPath(), new StompSessionHandlerAdapter() {})
                .get(5, TimeUnit.SECONDS);

        ws1.subscribe("/topic/"+spaceId, new StompFrameHandler() {
            @Override
            @NonNull
            public Type getPayloadType(@Nullable StompHeaders headers) {
                return String.class; // Assume the payload is a JSON string
            }

            @Override
            public void handleFrame(@Nullable StompHeaders headers, Object payload) {
                Object message = parseMessage(payload);
                if(message!=null)
                    addMessage(ws1Messages, message);
            }
        });

        ws2.subscribe("/topic/"+spaceId, new StompFrameHandler() {
            @Override
            @NonNull
            public Type getPayloadType(@Nullable StompHeaders headers) {
                return String.class; // Assume the payload is a JSON string
            }

            @Override
            public void handleFrame(@Nullable StompHeaders headers, Object payload) {
                Object message = parseMessage(payload);
                if(message!=null)
                    addMessage(ws2Messages, message);
            }
        });
    }

    private static Object parseMessage(Object payload){
        String jsonPayload = (String) payload;
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonPayload);
            String typeString = jsonNode.get("type").asText();
            MessageType type = MessageType.valueOf(typeString);
            return switch (type) {
                case SPACE_JOINED, SPACE_JOINED_BROADCAST
                        -> objectMapper.readValue(jsonPayload, JoinSpaceResponse.class);
                case JOIN
                        -> objectMapper.readValue(jsonPayload, JoinSpaceRequest.class);
                case MOVE, MOVE_REJECTED
                        -> objectMapper.readValue(jsonPayload, MovementResponse.class);
                case USER_LEFT
                        -> objectMapper.readValue(jsonPayload, LeaveSpaceResponse.class);
            };
        } catch (Exception e) {
            return null;
        }
    }

    private static final Object lock = new Object();
    // To safely add a message and notify waiting threads
    static void addMessage(List<Object> messages, Object message) {
        synchronized (lock) {
            messages.add(message);
            lock.notifyAll(); // Notify all waiting threads
        }
    }

    private CompletableFuture<Object> waitForAndPopLatestMessages(List<Object> messages) {
        return CompletableFuture.supplyAsync(() -> {
            synchronized (lock) {
                try {
                    // Wait with a timeout to prevent infinite waiting
                    long waitTime = 100;
                    long startTime = System.currentTimeMillis();

                    while (messages.isEmpty()) {
                        long remainingTime = waitTime - (System.currentTimeMillis() - startTime);
                        if (remainingTime <= 0) {
                            throw new TimeoutException("No message received within timeout period");
                        }
                        lock.wait(remainingTime);
                    }
                    // Extra safety check
                    return messages.remove(0);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted", e);
                } catch (TimeoutException e) {
                    throw new RuntimeException("Waiting for message timed out", e);
                }
            }
        });
    }

    @Order(1)
    @Test
    void getAckOnJoiningSpaceAndBroadCast() throws ExecutionException, InterruptedException, TimeoutException {
        //join a room
        JoinSpaceRequest ws1Message = JoinSpaceRequest.builder()
                .type(MessageType.JOIN)
                .payload(JoinSpaceRequestPayload.builder()
                        .spaceId(spaceId)
                        .token(adminToken)
                        .build())
                .build();

        JoinSpaceRequest ws2Message = JoinSpaceRequest.builder()
                .type(MessageType.JOIN)
                .payload(JoinSpaceRequestPayload.builder()
                        .spaceId(spaceId)
                        .token(userToken)
                        .build())
                .build();

        ws1.send("/app/space/"+spaceId,ws1Message);
        CompletableFuture<Object> ws1future = waitForAndPopLatestMessages(ws1Messages);
        JoinSpaceResponse ws1response = (JoinSpaceResponse)ws1future.get(100,TimeUnit.MILLISECONDS);
        adminX = ws1response.getPayload().getX();
        adminY = ws1response.getPayload().getY();

        ws2.send("/app/space/"+spaceId,ws2Message);
        CompletableFuture<Object> ws2future = waitForAndPopLatestMessages(ws2Messages);
        JoinSpaceResponse ws2response = (JoinSpaceResponse)ws2future.get(100,TimeUnit.MILLISECONDS);
        UserSpawn user;

        userX = ws2response.getPayload().getX();
        userY = ws2response.getPayload().getY();

        assertEquals(MessageType.SPACE_JOINED, ws1response.getType());
        assertEquals(MessageType.SPACE_JOINED, ws2response.getType());
        assertEquals(0, ws1response.getPayload().getUsers().size());
        assertEquals(1, ws2response.getPayload().getUsers().size());
        assertEquals(adminX, ws2response.getPayload().getUsers().get(0).getX());
        assertEquals(adminY, ws2response.getPayload().getUsers().get(0).getY());

        CompletableFuture<Object> joinBroadcast = waitForAndPopLatestMessages(ws1Messages);
        JoinSpaceResponse joinResponse = (JoinSpaceResponse)joinBroadcast.get(100,TimeUnit.MILLISECONDS);
        assertEquals(MessageType.SPACE_JOINED_BROADCAST, joinResponse.getType());
        assertEquals(1, joinResponse.getPayload().getUsers().size());
        assertEquals(userId, joinResponse.getPayload().getUsers().get(0).getUserId());
        assertEquals(userX, joinResponse.getPayload().getUsers().get(0).getX());
        assertEquals(userY, joinResponse.getPayload().getUsers().get(0).getY());


    }

    @Order(2)
    @Test
    void userShouldNotBeAbleToMoveOutOfBound() throws ExecutionException, InterruptedException, TimeoutException {
        //move
        MovementRequest sentMessage = MovementRequest.builder()
                .type(MessageType.MOVE)
                .payload(MovementRequestPayload.builder()
                        .x(1000000)
                        .y(2000000)
                        .userId(adminId)
                        .spaceId(spaceId)
                        .token("Bearer eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MzQ3MjE5NjgsImV4cCI6MTczNDgwODM2OCwiYXV0aG9yaXRpZXMiOiJST0xFX0FETUlOIiwiZW1haWwiOiJ3c3NwYWNlY29udHJvbGxlcmFkbWluIiwiaWQiOiI3OGE5NmJhZC02NmQzLTRlYzQtYTU5Yy01YjIzMGE5N2QyYTIifQ.ShA777WRQ_uj58Q4lFEPh4DYVcKwsSnRecZcbu7CuBQ")
                        .build())
                .build();

        ws1.send("/app/space/"+spaceId,sentMessage);
        CompletableFuture<Object> ws1future = waitForAndPopLatestMessages(ws1Messages);
        MovementResponse ws1response = (MovementResponse)ws1future.get(100,TimeUnit.MILLISECONDS);

        assertEquals(MessageType.MOVE_REJECTED, ws1response.getType());
        assertEquals(adminX, ws1response.getPayload().getX());
        assertEquals(adminY, ws1response.getPayload().getY());
    }

    @Order(3)
    @Test
    void userShouldNotBeAbleToJumpABlock() throws ExecutionException, InterruptedException, TimeoutException {
        //mvoe
        MovementRequest sentMessage = MovementRequest.builder()
                .type(MessageType.MOVE)
                .payload(MovementRequestPayload.builder()
                        .x(adminX+2)
                        .userId(adminId)
                        .y(adminY)
                        .spaceId(spaceId)
                        .token("Bearer eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MzQ3MjE5NjgsImV4cCI6MTczNDgwODM2OCwiYXV0aG9yaXRpZXMiOiJST0xFX0FETUlOIiwiZW1haWwiOiJ3c3NwYWNlY29udHJvbGxlcmFkbWluIiwiaWQiOiI3OGE5NmJhZC02NmQzLTRlYzQtYTU5Yy01YjIzMGE5N2QyYTIifQ.ShA777WRQ_uj58Q4lFEPh4DYVcKwsSnRecZcbu7CuBQ")
                        .build())
                .build();

        ws1.send("/app/space/"+spaceId,sentMessage);
        CompletableFuture<Object> ws1future = waitForAndPopLatestMessages(ws1Messages);
        MovementResponse ws1response = (MovementResponse)ws1future.get(100,TimeUnit.MILLISECONDS);

        assertEquals(MessageType.MOVE_REJECTED, ws1response.getType());
        assertEquals(adminX, ws1response.getPayload().getX());
        assertEquals(adminY, ws1response.getPayload().getY());
    }
    @Order(4)
    @Test
    void correctMoveShouldBeBroadcastedToOtherSocketsInTheSameSpace() throws ExecutionException, InterruptedException, TimeoutException {
        //move
        MovementRequest sentMessage = MovementRequest.builder()
                .type(MessageType.MOVE)
                .payload(MovementRequestPayload.builder()
                        .x(adminX+1)
                        .y(adminY)
                        .userId(adminId)
                        .spaceId(spaceId)
                        .token("Bearer eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MzQ3MjE5NjgsImV4cCI6MTczNDgwODM2OCwiYXV0aG9yaXRpZXMiOiJST0xFX0FETUlOIiwiZW1haWwiOiJ3c3NwYWNlY29udHJvbGxlcmFkbWluIiwiaWQiOiI3OGE5NmJhZC02NmQzLTRlYzQtYTU5Yy01YjIzMGE5N2QyYTIifQ.ShA777WRQ_uj58Q4lFEPh4DYVcKwsSnRecZcbu7CuBQ")
                        .build())
                .build();

        ws1.send("/app/space/"+spaceId,sentMessage);
        CompletableFuture<Object> ws2future = waitForAndPopLatestMessages(ws2Messages);
        MovementResponse ws2response = (MovementResponse)ws2future.get(100,TimeUnit.MILLISECONDS);
        //assert
        assertEquals(MessageType.MOVE,ws2response.getType());
        assertEquals(adminX, ws2response.getPayload().getX() + 1);
        assertEquals(adminY, ws2response.getPayload().getY());
        assertEquals(adminId, ws2response.getPayload().getUserId());
        assertEquals(spaceId, ws2response.getPayload().getSpaceId());
    }
    @Order(5)
    @Test
    void leaveMessageShouldBeBroadcasted() throws ExecutionException, InterruptedException, TimeoutException {
        //move
        if (ws1 != null && ws1.isConnected()) {
            ws1.disconnect();
        }

        CompletableFuture<Object> ws2future = waitForAndPopLatestMessages(ws2Messages);
        LeaveSpaceResponse leaveResponse = (LeaveSpaceResponse)ws2future.get(100,TimeUnit.MILLISECONDS);
        assertEquals(MessageType.USER_LEFT,leaveResponse.getType());
        assertEquals(adminId, leaveResponse.getPayload().getUserId());
    }
}

