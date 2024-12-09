package com.sharmachait.PrimaryBackend.controller;

import com.sharmachait.PrimaryBackend.models.dto.LoginDto;
import com.sharmachait.PrimaryBackend.models.entity.Role;
import com.sharmachait.PrimaryBackend.models.response.AuthResponse;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest {
    @LocalServerPort
    private int serverPort;

    LoginDto loginDto;

    @BeforeEach
    void setUp() {
        loginDto = new LoginDto();
        loginDto.setUsername("John");
        loginDto.setPassword("password");
        loginDto.setRole(Role.ROLE_USER);
    }

    @Order(1)
    @Test
    @DisplayName("User can be created only once")
    void register() {
        //arrange
        WebClient webClient = WebClient.create();
        //act
        ClientResponse clientResponse = webClient.post()
                .uri("http://localhost:" + serverPort + "/auth/signup")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginDto)
                .exchange()
                .block(); // Block to get the ClientResponse synchronously
        //assert
        assertNotNull(clientResponse, "Response object should not be null");
        assertEquals(clientResponse.statusCode(), HttpStatus.CREATED, "Status code should be 201 CREATED");

        // Extract and assert the response body
        AuthResponse response = clientResponse.bodyToMono(AuthResponse.class).block();
        assertNotNull(response, "Response object should not be null");
        assertNotNull(response.getJwt(), "JWT token should not be null");
        assertEquals(response.getStatus(), true, "Status should be true");
        assertEquals(response.getMessage(), "User registered successfully", "Message should match");
    }

    @Order(2)
    @Test
    @DisplayName("User can not be created")
    void registerDuplicate() {
        //arrange
        WebClient webClient = WebClient.create();
        //act
        ClientResponse clientResponse = webClient.post()
                .uri("http://localhost:" + serverPort + "/auth/signup")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginDto)
                .exchange()
                .block(); // Block to get the ClientResponse synchronously
        //assert
        assertNotNull(clientResponse, "Response object should not be null");
        assertEquals(clientResponse.statusCode(), HttpStatus.CONFLICT, "Status code should be CONFLICT");

        // Extract and assert the response body
        AuthResponse response = clientResponse.bodyToMono(AuthResponse.class).block();
        assertNotNull(response, "Response object should not be null");
        assertNull(response.getJwt(), "JWT token should be null");
        assertEquals(response.getStatus(), false, "Status should be false");
        assertEquals("Username already exists", response.getMessage(), "Message should match");
    }

    @Order(3)
    @Test
    @DisplayName("User can not be created with empty or null username")
    void registerEmptyUser() {
        //arrange
        loginDto.setUsername("");
        WebClient webClient = WebClient.create();
        //act
        ClientResponse clientResponse = webClient.post()
                .uri("http://localhost:" + serverPort + "/auth/signup")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginDto)
                .exchange()
                .block(); // Block to get the ClientResponse synchronously
        //assert
        assertNotNull(clientResponse, "Response object should not be null");
        assertEquals(HttpStatus.BAD_REQUEST, clientResponse.statusCode(), "Status code should be BAD_REQUEST");

        // Extract and assert the response body
        AuthResponse response = clientResponse.bodyToMono(AuthResponse.class).block();
        assertNotNull(response, "Response object should not be null");
        assertNull(response.getJwt(), "JWT token should be null");
        assertEquals(response.getStatus(), false, "Status should be false");
        assertEquals("Username cannot be null or empty", response.getMessage(), "Message should match");
    }

    @Order(4)
    @Test
    @DisplayName("login user")
    void login() {
        //arrange
        WebClient webClient = WebClient.create();
        //act
        ClientResponse clientResponse = webClient.post()
                .uri("http://localhost:" + serverPort + "/auth/signin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginDto)
                .exchange()
//                .bodyToMono(AuthResponse.class)
                .block();
        //assert
        assertNotNull(clientResponse, "Response object should not be null");
        assertEquals(clientResponse.statusCode(), HttpStatus.OK, "Status code should be 201 CREATED");

        AuthResponse response = clientResponse.bodyToMono(AuthResponse.class).block();
        assertNotNull(response, "Response object should not be null");
        assertNotNull(response.getJwt());
        assertEquals(response.getStatus(), true, "Status should match");
        assertEquals(response.getMessage(), "Logged in successfully", "Message should match");
    }

    @Order(5)
    @Test
    @DisplayName("login user should fail for incorrect credentials")
    void loginWrongPassword() {
        //arrange
        loginDto.setPassword(Math.random() + "");
        WebClient webClient = WebClient.create();
        //act
        ClientResponse clientResponse = webClient.post()
                .uri("http://localhost:" + serverPort + "/auth/signin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginDto)
                .exchange()
//                .bodyToMono(AuthResponse.class)
                .block();
        //assert
        assertNotNull(clientResponse, "Response object should not be null");
        assertEquals(clientResponse.statusCode(), HttpStatus.UNAUTHORIZED, "Status code should be 201 CREATED");

        AuthResponse response = clientResponse.bodyToMono(AuthResponse.class).block();
        assertNotNull(response, "Response object should not be null");
        assertNull(response.getJwt());
        assertEquals(response.getStatus(), false, "Status should match");
        assertEquals(response.getMessage(), "Unauthorized", "Message should match");
    }
}