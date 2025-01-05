package com.sharmachait.PrimaryBackend.controller;

import com.sharmachait.PrimaryBackend.models.dto.*;
import com.sharmachait.PrimaryBackend.models.entity.Role;
import com.sharmachait.PrimaryBackend.models.response.AuthResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArenaControllerTest {
    @Value("${local.server.port}")
    void setServerPort(int serverPort) {
        ArenaControllerTest.serverPort = serverPort;
    }
    private static int serverPort;
    static String adminToken;
    static String userToken;
    static String element1Id;
    static String element2Id;
    static String adminId;
    static String userId;
    static String mapId;
    static String spaceId;
    private static RestTemplate restTemplate;
    private static HttpHeaders headers = new HttpHeaders();
    @BeforeAll
    static void setUp() {
        // Step 1: Signup as admin
        restTemplate = new RestTemplate();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String signupUrl = "http://localhost:" + serverPort + "/auth/signup";

        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("arenacontrolleradmin");
        loginDto.setPassword("password");
        loginDto.setRole(Role.ROLE_ADMIN);
        HttpEntity<LoginDto> signupRequest = new HttpEntity<>(loginDto, headers);
        ResponseEntity<AuthResponse> signupResponse = restTemplate.postForEntity(signupUrl, signupRequest, AuthResponse.class);
        AuthResponse authResponse = signupResponse.getBody();
        assert authResponse != null : "Signup failed, response is null.";
        adminToken = authResponse.getJwt();
        adminId = authResponse.getUserId();

        // Step 1: Signup as User
        loginDto.setUsername("arenacontrolleruser");
        loginDto.setRole(Role.ROLE_USER);
        HttpEntity<LoginDto> signupRequestUser = new HttpEntity<>(loginDto, headers);
        ResponseEntity<AuthResponse> signupResponseUser = restTemplate.postForEntity(signupUrl, signupRequestUser, AuthResponse.class);
        AuthResponse authResponseUser = signupResponseUser.getBody();
        assert authResponseUser != null : "Signup failed, response is null.";
        userToken = authResponseUser.getJwt();
        userId = authResponseUser.getUserId();

        // Step 2: Create Elements
        String elementUrl = "http://localhost:" + serverPort + "/api/v1/admin/element";
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
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
        String mapUrl = "http://localhost:" + serverPort + "/api/v1/admin/map";
        GameMapDto mapDto = new GameMapDto();
        mapDto.setThumbnail("https://thumbnail.com/a.png");
        mapDto.setDimensions("100x200");
        mapDto.setName("test map");
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
                .y(21)
                .build();
        List<MapElementDto> l = List.of(mapElement1, mapElement2, mapElement3, mapElement4);
        mapDto.setMapElements(l);
        HttpEntity<GameMapDto> mapRequest = new HttpEntity<>(mapDto, headers);
        ResponseEntity<GameMapDto> mapResponse = restTemplate.postForEntity(mapUrl, mapRequest, GameMapDto.class);
        mapId = mapResponse.getBody().getId();

        //step 4: create a space
        String url = "http://localhost:" + serverPort + "/api/v1/space";
        SpaceDto spaceDto = SpaceDto.builder()
                .name("Test Space")
                .dimensions("100x200")
                .mapId(mapId)
                .build();
        HttpEntity<SpaceDto> spacerequest = new HttpEntity<>(spaceDto, headers);
        ResponseEntity<SpaceDto> spacedto = restTemplate.postForEntity(url, spacerequest, SpaceDto.class);
        spaceId = spacedto.getBody().getId();
    }
    @Order(1)
    @Test
    @DisplayName("Incorrect space id should throw 400")
    void incorrectSpaceIdShouldReturn400() {
        //arrange
        String Spaceurl = "http://localhost:" + serverPort + "/api/v1/space/randombullshitid";
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + userToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        //act
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            restTemplate.exchange(
                    Spaceurl, HttpMethod.GET, request, SpaceDto.class);
        }, "Expected postForEntity to throw HttpClientErrorException");

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(), "Expected BAD_REQUEST status code");

    }
    @Order(2)
    @Test
    @DisplayName("Correct space id should return 200")
    void CorrectSpaceIdShouldReturn200() {
        //arrange
        String Spaceurl = "http://localhost:" + serverPort + "/api/v1/space/" + spaceId;

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + userToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        //act
        ResponseEntity<SpaceDto> spaceResponse = restTemplate.exchange(
                Spaceurl, HttpMethod.GET, request, SpaceDto.class);

        assertEquals(HttpStatus.OK, spaceResponse.getStatusCode(), "Expected a OK status for valid token");
        assertNotNull(spaceResponse.getBody(), "Space returned null");
        assertEquals("100x200", spaceResponse.getBody().getDimensions());
        assertEquals(4,spaceResponse.getBody().getElements().size());
    }
    @Order(3)
    @Test
    @DisplayName("Able to delete element")
    void ableToDeleteElement() {
        //arrange
        String Spaceurl = "http://localhost:" + serverPort + "/api/v1/space/" + spaceId;

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + adminToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        //act
        ResponseEntity<SpaceDto> spaceResponse = restTemplate.exchange(
                Spaceurl, HttpMethod.GET, request, SpaceDto.class);

        String elementId = spaceResponse.getBody().getElements().get(0).getId();

        Spaceurl = "http://localhost:" + serverPort + "/api/v1/space/element/"+elementId;

        SpaceDto deleteDto = new SpaceDto();
        deleteDto.setId(spaceId);
        SpaceElementDto spaceelementtodelete =SpaceElementDto.builder()
                .elementId(elementId)
                .build();

        deleteDto.setElements(List.of(spaceelementtodelete));
        HttpEntity<SpaceDto> deleteRequest = new HttpEntity<>(deleteDto, headers);
        //act
        ResponseEntity<SpaceDto> deleteResponse = restTemplate.exchange(
                Spaceurl, HttpMethod.DELETE, deleteRequest, SpaceDto.class);
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode(), "Expected a OK status for valid token");
        assertEquals(3,deleteResponse.getBody().getElements().size());
    }

    @Order(4)
    @Test
    @DisplayName("Able to add an element")
    void ableToAddElement() {
        //arrange
        String Spaceurl = "http://localhost:" + serverPort + "/api/v1/space/element/"+spaceId;
        SpaceElementDto addDto = SpaceElementDto.builder()
                .elementId(element1Id)
                .isStatic(true)
                .x(50)
                .y(20)
                .build();

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + adminToken);
        HttpEntity<SpaceElementDto> addRequest = new HttpEntity<>(addDto, headers);
        //act
        ResponseEntity<SpaceDto> addResponse = restTemplate.exchange(
                Spaceurl, HttpMethod.POST, addRequest, SpaceDto.class);

        assertEquals(HttpStatus.CREATED, addResponse.getStatusCode(), "Expected a CREATED status for valid token");
        assertNotNull(addResponse.getBody(), "Space returned null");
        assertEquals("100x200", addResponse.getBody().getDimensions());
        assertEquals(4,addResponse.getBody().getElements().size());
    }

    @Order(5)
    @Test
    @DisplayName("Not able to add an element out of bounds")
    void noAbleToAddElementOutOfBounds() {
        //arrange
        String Spaceurl = "http://localhost:" + serverPort + "/api/v1/space/element/"+spaceId;

        SpaceElementDto addDto = SpaceElementDto.builder()
                .elementId(element1Id)
                .isStatic(true)
                .x(1000000)
                .y(2000000)
                .build();

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + adminToken);
        HttpEntity<SpaceElementDto> addRequest = new HttpEntity<>(addDto, headers);
        //act
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            restTemplate.exchange(
                    Spaceurl, HttpMethod.POST, addRequest, SpaceDto.class);
        }, "Expected postForEntity to throw HttpClientErrorException");
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(), "Expected a BAD_REQUEST status for valid token");
    }
}