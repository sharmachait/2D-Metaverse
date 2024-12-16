package com.sharmachait.PrimaryBackend.controller;

import com.sharmachait.PrimaryBackend.models.dto.*;
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
class AdminControllerTest {

    @Value("${local.server.port}")
    void setServerPort(int serverPort) {
        AdminControllerTest.serverPort = serverPort;
    }
    private static int serverPort;
    static String adminToken;
    static String userToken;
    static String adminId;
    static String userId;
    private static RestTemplate restTemplate;
    private static HttpHeaders headers = new HttpHeaders();
    @BeforeAll
    static void setUp() {
        // Step 1: Signup as admin
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("admincontrolleradmin");
        loginDto.setPassword("password");
        loginDto.setRole(Role.ROLE_ADMIN);
        restTemplate = new RestTemplate();
        String signupUrl = "http://localhost:" + serverPort + "/auth/signup";

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginDto> signupRequest = new HttpEntity<>(loginDto, headers);
        ResponseEntity<AuthResponse> signupResponse = restTemplate.postForEntity(signupUrl, signupRequest, AuthResponse.class);
        AuthResponse authResponse = signupResponse.getBody();
        assert authResponse != null : "Signup failed, response is null.";
        adminToken = authResponse.getJwt();
        adminId = authResponse.getUserId();

        // Step 1: Signup as User
        loginDto = new LoginDto();
        loginDto.setUsername("admincontrolleruser");
        loginDto.setRole(Role.ROLE_USER);
        HttpEntity<LoginDto> signupRequestUser = new HttpEntity<>(loginDto, headers);
        ResponseEntity<AuthResponse> signupResponseUser = restTemplate.postForEntity(signupUrl, signupRequestUser, AuthResponse.class);
        AuthResponse authResponseUser = signupResponseUser.getBody();
        assert authResponseUser != null : "Signup failed, response is null.";
        userToken = authResponseUser.getJwt();
        userId = authResponseUser.getUserId();

    }
    @Order(1)
    @Test
    @DisplayName("User not able to access admin endpoints")
    void userNotAbleToAccessAdminEndpoints() {
        //arrange
        String elementUrl = "http://localhost:" + serverPort + "/api/v1/admin/element";
        headers.add("Authorization", "Bearer " + userToken);
        ElementDto elementDto = new ElementDto();
        elementDto.setImageUrl("https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcRCRca3wAR4zjPPTzeIY9rSwbbqB6bB2hVkoTXN4eerXOIkJTG1GpZ9ZqSGYafQPToWy_JTcmV5RHXsAsWQC3tKnMlH_CsibsSZ5oJtbakq&usqp=CAE");
        elementDto.setWidth(1);
        elementDto.setHeight(1);
        elementDto.setIsStatic(true);
        HttpEntity<ElementDto> elementRequest = new HttpEntity<>(elementDto, headers);
        //act
        ResponseEntity<ElementDto> elementResponse = restTemplate.postForEntity(elementUrl, elementRequest, ElementDto.class);
        //assert
        assertEquals(HttpStatus.UNAUTHORIZED, elementResponse.getStatusCode(), "Expected a UNAUTHORIZED status for valid token");
        assertNull(elementResponse.getBody(), "Space did not return null");

        //arrange
        String mapUrl = "http://localhost:" + serverPort + "/api/v1/admin/map";
        GameMapDto mapDto = new GameMapDto();
        mapDto.setThumbnail("https://thumbnail.com/a.png");
        mapDto.setDimensions("100x200");
        HttpEntity<GameMapDto> mapRequest = new HttpEntity<>(mapDto, headers);
        //act
        ResponseEntity<GameMapDto> mapResponse = restTemplate.postForEntity(mapUrl, mapRequest, GameMapDto.class);
        //assert
        assertEquals(HttpStatus.UNAUTHORIZED, mapResponse.getStatusCode(), "Expected a UNAUTHORIZED status for valid token");
        assertNull(mapResponse.getBody(), "map did not return null");

        //arrange
        String avatarUrl = "http://localhost:" + serverPort + "/api/v1/admin/avatar";
        AvatarDto avatarDto = new AvatarDto();
        avatarDto.setName("testAvatar");
        avatarDto.setImageUrl("testUrl");
        HttpEntity<AvatarDto> avatarRequest = new HttpEntity<>(avatarDto, headers);
        //act
        ResponseEntity<AvatarDto> avatarResponse = restTemplate.postForEntity(avatarUrl, avatarRequest, AvatarDto.class);
        //assert
        assertEquals(HttpStatus.UNAUTHORIZED, avatarResponse.getStatusCode(), "Expected a UNAUTHORIZED status for valid token");
        assertNull(avatarResponse.getBody(), "avatar did not return null");


        //arrange
        String elementUpdateUrl = "http://localhost:" + serverPort + "/api/v1/admin/element/elementId";
        ElementDto elementUpdateDto = new ElementDto();
        elementUpdateDto.setImageUrl("https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcRCRca3wAR4zjPPTzeIY9rSwbbqB6bB2hVkoTXN4eerXOIkJTG1GpZ9ZqSGYafQPToWy_JTcmV5RHXsAsWQC3tKnMlH_CsibsSZ5oJtbakq&usqp=CAE");
        elementUpdateDto.setWidth(1);
        elementUpdateDto.setHeight(1);
        elementUpdateDto.setIsStatic(true);
        HttpEntity<ElementDto> elementUpdateRequest = new HttpEntity<>(elementUpdateDto, headers);
        //act
        ResponseEntity<ElementDto> elementUpdateResponse = restTemplate.exchange(elementUpdateUrl, HttpMethod.PUT, elementUpdateRequest, ElementDto.class);
        //assert
        assertEquals(HttpStatus.UNAUTHORIZED, elementUpdateResponse.getStatusCode(), "Expected a UNAUTHORIZED status for valid token");
        assertNull(elementUpdateResponse.getBody(), "Space did not return null");
    }
    @Order(2)
    @Test
    @DisplayName("Only Admin Should be able to create an element")
    void adminShouldCreateElement() {
        //arrange
        String elementUrl = "http://localhost:" + serverPort + "/api/v1/admin/element";
        headers.add("Authorization", "Bearer " + adminToken);
        ElementDto elementDto = new ElementDto();
        elementDto.setImageUrl("https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcRCRca3wAR4zjPPTzeIY9rSwbbqB6bB2hVkoTXN4eerXOIkJTG1GpZ9ZqSGYafQPToWy_JTcmV5RHXsAsWQC3tKnMlH_CsibsSZ5oJtbakq&usqp=CAE");
        elementDto.setWidth(1);
        elementDto.setHeight(1);
        elementDto.setIsStatic(true);
        HttpEntity<ElementDto> elementRequest = new HttpEntity<>(elementDto, headers);
        //act
        ResponseEntity<ElementDto> elementResponse = restTemplate.postForEntity(elementUrl, elementRequest, ElementDto.class);
        //assert
        assertEquals(HttpStatus.CREATED, elementResponse.getStatusCode(), "Expected a CREATED status for valid token");
        assertNotNull(elementResponse.getBody(), "element returned null");

        //arrange
        String mapUrl = "http://localhost:" + serverPort + "/api/v1/admin/map";
        GameMapDto mapDto = new GameMapDto();
        mapDto.setThumbnail("https://thumbnail.com/a.png");
        mapDto.setDimensions("100x200");
        HttpEntity<GameMapDto> mapRequest = new HttpEntity<>(mapDto, headers);
        //act
        ResponseEntity<GameMapDto> mapResponse = restTemplate.postForEntity(mapUrl, mapRequest, GameMapDto.class);
        //assert
        assertEquals(HttpStatus.CREATED, mapResponse.getStatusCode(), "Expected a CREATED status for valid token");
        assertNotNull(mapResponse.getBody(), "map returned null");

        //arrange
        String avatarUrl = "http://localhost:" + serverPort + "/api/v1/admin/avatar";
        AvatarDto avatarDto = new AvatarDto();
        avatarDto.setName("testAvatar");
        avatarDto.setImageUrl("testUrl");
        HttpEntity<AvatarDto> avatarRequest = new HttpEntity<>(avatarDto, headers);
        //act
        ResponseEntity<AvatarDto> avatarResponse = restTemplate.postForEntity(avatarUrl, avatarRequest, AvatarDto.class);
        //assert
        assertEquals(HttpStatus.CREATED, avatarResponse.getStatusCode(), "Expected a CREATED status for valid token");
        assertNotNull(avatarResponse.getBody(), "avatar returned null");
    }
    @Order(3)
    @Test
    @DisplayName("Only Admin Should be able to update the image url for an element")
    void adminShouldUpdateElement() {
        //arrange
        String elementUrl = "http://localhost:" + serverPort + "/api/v1/admin/element";
        headers.add("Authorization", "Bearer " + adminToken);
        ElementDto elementDto = new ElementDto();
        elementDto.setImageUrl("https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcRCRca3wAR4zjPPTzeIY9rSwbbqB6bB2hVkoTXN4eerXOIkJTG1GpZ9ZqSGYafQPToWy_JTcmV5RHXsAsWQC3tKnMlH_CsibsSZ5oJtbakq&usqp=CAE");
        elementDto.setWidth(1);
        elementDto.setHeight(1);
        elementDto.setIsStatic(true);
        HttpEntity<ElementDto> elementRequest = new HttpEntity<>(elementDto, headers);
        //act
        ResponseEntity<ElementDto> elementResponse = restTemplate.postForEntity(
                elementUrl, elementRequest, ElementDto.class);

        elementDto = elementResponse.getBody();
        elementDto.setImageUrl("updated");
        String elementUpdateUrl = "http://localhost:" + serverPort + "/api/v1/admin/element/"+elementDto.getId();
        HttpEntity<ElementDto> elementUpdateRequest = new HttpEntity<>(elementDto, headers);
        ResponseEntity<ElementDto> elementUpdateResponse = restTemplate.exchange(
                elementUpdateUrl, HttpMethod.PUT, elementUpdateRequest, ElementDto.class);
        //assert
        assertEquals(HttpStatus.OK, elementUpdateResponse.getStatusCode(), "Expected an OK status for valid token");
        assertNotNull(elementUpdateResponse.getBody(), "Response body should not be null");
        assertEquals("updated", elementUpdateResponse.getBody().getImageUrl(), "Image URL should be updated");
    }
}