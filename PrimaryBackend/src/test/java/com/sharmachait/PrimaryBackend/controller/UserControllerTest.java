package com.sharmachait.PrimaryBackend.controller;

import com.sharmachait.PrimaryBackend.models.dto.AvatarDto;
import com.sharmachait.PrimaryBackend.models.dto.LoginDto;
import com.sharmachait.PrimaryBackend.models.dto.UserDto;
import com.sharmachait.PrimaryBackend.models.entity.Role;
import com.sharmachait.PrimaryBackend.models.entity.User;
import org.junit.jupiter.api.*;
import com.sharmachait.PrimaryBackend.models.response.AuthResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {
  @Value("${local.server.port}")
  void setServerPort(int serverPort) {
    UserControllerTest.serverPort = serverPort;
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
    loginDto.setUsername("usercontroller");
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
  @DisplayName("Update metadata with valid jwt invalid avatar id should fail")
  void updateMetadataWithValidJwtInvalidAvatarIdShouldFail() {
    // arrange
    String url = "http://localhost:" + serverPort + "/api/v1/user/metadata";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Authorization", "Bearer " + token);

    UserDto userDto = new UserDto();
    userDto.setAvatarId("incorrect avatar id");

    HttpEntity<UserDto> request = new HttpEntity<>(userDto, headers);

    // act and assert
    // Act and Assert
    HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
      restTemplate.postForEntity(url, request, UserDto.class);
    });

    // Assert that the response status is BAD_REQUEST (400)
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(),
        "Expected a Bad Request status for invalid avatar ID");
  }

  @Order(2)
  @Test
  @DisplayName("Update metadata with valid jwt valid avatar id should pass")
  void updateMetadataWithValidJwtValidAvatarIdShouldPass() {
    // arrange
    String url = "http://localhost:" + serverPort + "/api/v1/user/metadata";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Authorization", "Bearer " + token);

    UserDto userDto = new UserDto();
    userDto.setAvatarId(avatarId);

    HttpEntity<UserDto> request = new HttpEntity<>(userDto, headers);
    // act and assert
    ResponseEntity<UserDto> userUpdatedResponse = restTemplate.postForEntity(url, request, UserDto.class);

    assertNotNull(userUpdatedResponse.getBody());
    assertEquals(avatarId, userUpdatedResponse.getBody().getAvatarId(), "Avatar id mismatch");
    assertEquals(HttpStatus.OK, userUpdatedResponse.getStatusCode(), "Expected a OK status for valid avatar ID");
  }

  @Order(3)
  @Test
  @DisplayName("Update metadata with invalid jwt valid avatar id should fail")
  void updateMetadataWithInvalidJwtValidAvatarIdShouldFail() {
    // arrange
    String url = "http://localhost:" + serverPort + "/api/v1/user/metadata";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    // headers.add("Authorization", "Bearer " + token);

    UserDto userDto = new UserDto();
    userDto.setAvatarId(avatarId);

    HttpEntity<UserDto> request = new HttpEntity<>(userDto, headers);
    // act and assert
    HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
      restTemplate.postForEntity(url, request, UserDto.class);
    });
    assertEquals(exception.getStatusCode(), HttpStatus.UNAUTHORIZED, "status code should be UNAUHTORIZED");

  }

  @Order(4)
  @Test
  @DisplayName("Get Avatar information for user Ids")
  void getAvatarInformationForUserBulk() {
    // arrange

    String updateUrl = "http://localhost:" + serverPort + "/api/v1/user/metadata";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Authorization", "Bearer " + token);

    UserDto userDto = new UserDto();
    userDto.setAvatarId(avatarId);

    HttpEntity<UserDto> updateRequest = new HttpEntity<>(userDto, headers);
    // act and assert
    ResponseEntity<UserDto> userUpdatedResponse = restTemplate.postForEntity(updateUrl, updateRequest, UserDto.class);
    System.out.println("POST response user ID: " + userUpdatedResponse.getBody().getId());
    System.out.println("GET request user ID parameter: " + userId);

    String url = String.format("http://localhost:%s/api/v1/user/metadata/bulk?ids=%s",
        serverPort,
        userId);

    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Authorization", "Bearer " + token);

    HttpEntity<String> request = new HttpEntity<>(headers);
    // act and assert
    ResponseEntity<List<UserDto>> response = restTemplate.exchange(url, HttpMethod.GET, request,
        new ParameterizedTypeReference<List<UserDto>>() {
        });
    List<UserDto> avatars = response.getBody();

    assertEquals(HttpStatus.OK, response.getStatusCode(), "status code should be OK");
    assertEquals(1, avatars.size(), "Size of response body should be 1");
    assertEquals(avatarId, avatars.get(0).getAvatarId(), "avatar ids should match");
  }

  @Order(5)
  @Test
  @DisplayName("Get Avatar information for user Id")
  void getAvatarInformationForUserId() {
    // arrange

    String url = "http://localhost:" +
        serverPort +
        "/api/v1/user/metadata";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Authorization", "Bearer " + token);

    HttpEntity<String> request = new HttpEntity<>(headers);
    // act and assert
    ResponseEntity<UserDto> response = restTemplate.exchange(url, HttpMethod.GET, request, UserDto.class);

    assertEquals(response.getStatusCode(), HttpStatus.OK, "status code should be OK");
  }

}