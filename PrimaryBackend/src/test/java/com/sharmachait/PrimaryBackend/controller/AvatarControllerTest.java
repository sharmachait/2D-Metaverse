package com.sharmachait.PrimaryBackend.controller;

import com.sharmachait.PrimaryBackend.models.dto.AvatarDto;
import com.sharmachait.PrimaryBackend.models.dto.LoginDto;
import com.sharmachait.PrimaryBackend.models.entity.Role;
import com.sharmachait.PrimaryBackend.models.response.AuthResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AvatarControllerTest {
  @Value("${local.server.port}")
  void setServerPort(int serverPort) {
    AvatarControllerTest.serverPort = serverPort;
  }

  private static int serverPort;
  static String token;
  static String avatarId;
  static String userId;
  private static RestTemplate restTemplate;

  @BeforeAll
  static void setUp() {
    // Step 1: Signup
    LoginDto loginDto = new LoginDto();
    loginDto.setUsername("avatarcontroller");
    loginDto.setPassword("password");
    loginDto.setRole(Role.ROLE_ADMIN);

    restTemplate = new RestTemplate();
    String signupUrl = "http://localhost:" + serverPort + "/auth/signup";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<LoginDto> signupRequest = new HttpEntity<>(loginDto, headers);
    ResponseEntity<AuthResponse> signupResponse = restTemplate.postForEntity(signupUrl, signupRequest,
        AuthResponse.class);

    AuthResponse authResponse = signupResponse.getBody();
    assert authResponse != null : "Signup failed, response is null.";
    token = authResponse.getJwt();
    userId = authResponse.getUserId();
    // Step 2: Create Avatar
    String avatarUrl = "http://localhost:" + serverPort + "/api/v1/admin/avatar";
    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Authorization", "Bearer " + token);

    AvatarDto avatarDto = new AvatarDto();
    avatarDto.setName("testAvatar");
    avatarDto.setImageUrl("testUrl");

    HttpEntity<AvatarDto> avatarRequest = new HttpEntity<>(avatarDto, headers);
    ResponseEntity<AvatarDto> avatarResponse = restTemplate.postForEntity(avatarUrl, avatarRequest, AvatarDto.class);

    AvatarDto avatar = avatarResponse.getBody();
    assert avatar != null : "Avatar creation failed, response is null.";
    avatarId = avatar.getId();
  }

  @Order(1)
  @Test
  @DisplayName("Get All avatars should return more than one avatar")
  void getAvatarsInformation() {
    // arrange

    String url = "http://localhost:" +
        serverPort +
        "/api/v1/avatar";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Authorization", "Bearer " + token);

    HttpEntity<String> request = new HttpEntity<>(headers);
    // act and assert
    ResponseEntity<List<AvatarDto>> response = restTemplate.exchange(url, HttpMethod.GET, request,
        new ParameterizedTypeReference<List<AvatarDto>>() {
        });
    List<AvatarDto> avatars = response.getBody();

    assertEquals(response.getStatusCode(), HttpStatus.CREATED, "status code should be OK");
    assertNotEquals(0, avatars.size(), "Size of response body should be greater than 0");
    AvatarDto avatar = avatars.stream()
        .filter(a -> a.getId().equals(avatarId))
        .findFirst()
        .orElse(null);

    assertNotNull(avatar, "Avatar should not be null");
  }
}