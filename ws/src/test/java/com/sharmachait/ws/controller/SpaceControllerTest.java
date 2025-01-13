package com.sharmachait.ws.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharmachait.ws.models.dto.*;
import com.sharmachait.ws.models.entity.Role;
import com.sharmachait.ws.models.messages.requestMessages.joinSpace.JoinSpaceRequest;
import com.sharmachait.ws.models.messages.MessageType;
import com.sharmachait.ws.models.messages.requestMessages.joinSpace.JoinSpaceRequestPayload;
import com.sharmachait.ws.models.messages.requestMessages.movement.MovementRequest;
import com.sharmachait.ws.models.messages.requestMessages.movement.MovementRequestPayload;
import com.sharmachait.ws.models.messages.responseMessages.joinedSpace.JoinSpaceResponse;
import com.sharmachait.ws.models.messages.responseMessages.joinedSpace.JoinSpaceResponsePayload;
import com.sharmachait.ws.models.messages.responseMessages.leaveSpace.LeaveSpaceResponse;
import com.sharmachait.ws.models.messages.responseMessages.movement.MovementResponse;
import com.sharmachait.ws.models.messages.responseMessages.movement.MovementResponsePayload;
import com.sharmachait.ws.models.response.AuthResponse;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpaceControllerTest {
    @Value("${local.server.port}")
    void setServerPort(int serverPort) {
        SpaceControllerTest.serverPort = serverPort;
    }

    private static int serverPort;
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
        int apiPort = 5455;
        String signupUrl = "http://localhost:" + apiPort + "/auth/signup";

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginDto> signupRequest = new HttpEntity<>(loginDto, headers);
        ResponseEntity<AuthResponse> signupResponse = restTemplate.postForEntity(signupUrl, signupRequest, AuthResponse.class);
        AuthResponse authResponse = signupResponse.getBody();
        assert authResponse != null : "Signup failed, response is null.";
        adminToken = authResponse.getJwt();
        adminId = authResponse.getUserId();

        // Step 1: Signup as UserDto
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
        headers = new HttpHeaders();
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

    private static StompSession ws1,ws2;
    private static List<Object> ws1Messages, ws2Messages;

    static void setUpWs() throws ExecutionException, InterruptedException, TimeoutException {

        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);

        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(new ObjectMapper());

        stompClient.setMessageConverter(converter);

//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        ws1Messages = new ArrayList<>();
        ws2Messages = new ArrayList<>();

        ws1 = stompClient.connectAsync(getWsPath(), new StompSessionHandlerAdapter() {})
                .get(10, TimeUnit.SECONDS);
        ws2 = stompClient.connectAsync(getWsPath(), new StompSessionHandlerAdapter() {})
                .get(10, TimeUnit.SECONDS);

        Thread.sleep(500);
        CountDownLatch subscriptionLatch = new CountDownLatch(2);
        StompHeaders headers1 = new StompHeaders();
        headers1.setDestination("/topic/space/" + spaceId);
        ws1.subscribe(headers1, new StompFrameHandler() {
            @Override
            @NonNull
            public Type getPayloadType(@Nullable StompHeaders headers) {
                return byte[].class; // Assume the payload is a JSON string
            }

            @Override
            public void handleFrame(@Nullable StompHeaders headers, Object payload) {
                Object message = parseMessage(payload);
                if(message!=null)
                    addMessage(ws1Messages, message);
            }
        });
        subscriptionLatch.countDown();
        StompHeaders headers2 = new StompHeaders();
        headers2.setDestination("/topic/space/" + spaceId);

        ws2.subscribe(headers2, new StompFrameHandler() {
            @Override
            @NonNull
            public Type getPayloadType(@Nullable StompHeaders headers) {
                return byte[].class; // Assume the payload is a JSON string
            }

            @Override
            public void handleFrame(@Nullable StompHeaders headers, Object payload) {
                Object message = parseMessage(payload);
                if(message!=null)
                    addMessage(ws2Messages, message);
            }
        });
        subscriptionLatch.countDown();
        if (!subscriptionLatch.await(5, TimeUnit.SECONDS)) {
            throw new TimeoutException("Failed to complete subscriptions within timeout");
        }
    }

    private static Object parseMessage(Object payload){

        try{
            String jsonPayload;
            if (payload instanceof byte[]) {
                jsonPayload = new String((byte[]) payload, StandardCharsets.UTF_8);
            } else if (payload instanceof String) {
                jsonPayload = (String) payload;
            } else {
                log.error("Unexpected payload type: {}", payload.getClass());
                return null;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonPayload);
            String typeString = jsonNode.get("type").asText();
            MessageType type = MessageType.valueOf(typeString);
            return switch (type) {
                case SPACE_JOINED, SPACE_JOINED_BROADCAST, BAD_REQUEST
                        -> objectMapper.readValue(jsonPayload, JoinSpaceResponse.class);
                case JOIN
                        -> objectMapper.readValue(jsonPayload, JoinSpaceRequest.class);
                case MOVE, MOVE_REJECTED
                        -> objectMapper.readValue(jsonPayload, MovementResponse.class);
                case USER_LEFT
                        -> objectMapper.readValue(jsonPayload, LeaveSpaceResponse.class);
                case CHAT -> objectMapper.readValue(jsonPayload, ChatMessageEntityDto.class);
            };
        } catch (Exception e) {

            return e;
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
                    long waitTime = 10000;
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

    @AfterAll
    static void tearDown() {
        if (ws1 != null) ws1.disconnect();
        if (ws2 != null) ws2.disconnect();
        // Clean up created test data from database
    }

    @Order(1)
    @Test
    void getAckOnJoiningSpaceAndBroadCast() throws ExecutionException, InterruptedException, TimeoutException {
        //join a room
        JoinSpaceRequest ws1Message = JoinSpaceRequest.builder()
                .type(MessageType.JOIN)
                .payload(JoinSpaceRequestPayload.builder()
                        .spaceId(spaceId)
                        .userId(adminId)
                        .token("Bearer "+adminToken)
                        .build())
                .build();

        JoinSpaceRequest ws2Message = JoinSpaceRequest.builder()
                .type(MessageType.JOIN)
                .payload(JoinSpaceRequestPayload.builder()
                        .spaceId(spaceId)
                        .userId(userId)
                        .token("Bearer "+userToken)
                        .build())
                .build();
        StompHeaders sendHeaders1 = new StompHeaders();
        sendHeaders1.setDestination("/app/space");
        ws1.send(sendHeaders1,ws1Message);
        Thread.sleep(1000);
        CompletableFuture<Object> ws1future = waitForAndPopLatestMessages(ws1Messages);
        JoinSpaceResponse ws1response = (JoinSpaceResponse)ws1future.get(10000,TimeUnit.MILLISECONDS);
        JoinSpaceResponsePayload res1 = (JoinSpaceResponsePayload)(ws1response.getPayload());
        assertEquals(MessageType.SPACE_JOINED, ws1response.getType());
        assertEquals(0, res1.getUsers().size());
        CompletableFuture<Object> ws2future = waitForAndPopLatestMessages(ws2Messages);
        JoinSpaceResponse ws2response = (JoinSpaceResponse)ws2future.get(10000,TimeUnit.MILLISECONDS);
        JoinSpaceResponsePayload res2 = (JoinSpaceResponsePayload)(ws2response.getPayload());
        assertEquals(MessageType.SPACE_JOINED, ws2response.getType());
        assertEquals(0, res2.getUsers().size());

        adminX = res1.getX();
        adminY = res1.getY();

        StompHeaders sendHeaders2 = new StompHeaders();
        sendHeaders2.setDestination("/app/space");
        ws2.send(sendHeaders2, ws2Message);
        Thread.sleep(1000);
        ws2future = waitForAndPopLatestMessages(ws2Messages);
        ws2response = (JoinSpaceResponse)ws2future.get(10000,TimeUnit.MILLISECONDS);
        res2 = (JoinSpaceResponsePayload)(ws2response.getPayload());
        assertEquals(MessageType.SPACE_JOINED, ws2response.getType());
        assertEquals(1, res2.getUsers().size());
        ws1future = waitForAndPopLatestMessages(ws1Messages);
        ws1response = (JoinSpaceResponse)ws1future.get(10000,TimeUnit.MILLISECONDS);
        res1 = (JoinSpaceResponsePayload)(ws1response.getPayload());
        assertEquals(MessageType.SPACE_JOINED, ws1response.getType());
        assertEquals(1, res1.getUsers().size());

        userX = res2.getX();
        userY = res2.getY();

        assertEquals(adminX, res2.getUsers().get(0).getX());
        assertEquals(adminY, res2.getUsers().get(0).getY());
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
        MovementResponsePayload res = (MovementResponsePayload)ws1response.getPayload();
        assertEquals(MessageType.MOVE_REJECTED, ws1response.getType());
        assertEquals(adminX, res.getX());
        assertEquals(adminY, res.getY());
    }

    @Order(3)
    @Test
    void userShouldNotBeAbleToJumpABlock() throws ExecutionException, InterruptedException, TimeoutException {
        //move
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
        MovementResponsePayload res = (MovementResponsePayload)ws1response.getPayload();
        assertEquals(MessageType.MOVE_REJECTED, ws1response.getType());
        assertEquals(adminX, res.getX());
        assertEquals(adminY, res.getY());
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
        MovementResponsePayload res = (MovementResponsePayload)ws2response.getPayload();
        //assert
        assertEquals(MessageType.MOVE,ws2response.getType());
        assertEquals(adminX, res.getX() + 1);
        assertEquals(adminY, res.getY());
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

