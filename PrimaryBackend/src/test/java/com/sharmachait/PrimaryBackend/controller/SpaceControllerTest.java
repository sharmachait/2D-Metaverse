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
class SpaceControllerTest {
    @Value("${local.server.port}")
    void setServerPort(int serverPort) {
        SpaceControllerTest.serverPort = serverPort;
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
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("spacecontrolleradmin");
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
        loginDto.setUsername("spacecontrolleruser");
        loginDto.setRole(Role.ROLE_USER);
        HttpEntity<LoginDto> signupRequestUser = new HttpEntity<>(loginDto, headers);
        ResponseEntity<AuthResponse> signupResponseUser = restTemplate.postForEntity(signupUrl, signupRequestUser, AuthResponse.class);
        AuthResponse authResponseUser = signupResponseUser.getBody();
        assert authResponseUser != null : "Signup failed, response is null.";
        userToken = authResponseUser.getJwt();
        userId = authResponseUser.getUserId();

        // Step 2: Create Elements
        String elementUrl = "http://localhost:" + serverPort + "/api/v1/admin/element";
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
    }

    @Order(1)
    @Test
    @DisplayName("User is able to create a space")
    void userIsAbleToCreateSpace() {
        //arrange
        String url = "http://localhost:" + serverPort + "/api/v1/space";

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + userToken);

        SpaceDto spaceDto = SpaceDto.builder()
                .name("Test Space")
                .dimensions("100x200")
                .mapId(mapId)
                .build();

        HttpEntity<SpaceDto> request = new HttpEntity<>(spaceDto, headers);

        //act
        ResponseEntity<SpaceDto> spaceResponse = restTemplate.postForEntity(url, request, SpaceDto.class);
        assertEquals(HttpStatus.CREATED, spaceResponse.getStatusCode(), "Expected a CREATED status for valid token");
        assertNotNull(spaceResponse.getBody(), "Space returned null");
        assertNotNull(spaceResponse.getBody().getId(), "Space returned null id");
    }

    @Order(2)
    @Test
    @DisplayName("User is able to create a space without mapId (empty space)")
    void userIsAbleToCreateSpaceWithoutMapId() {
        //arrange
        String url = "http://localhost:" + serverPort + "/api/v1/space";

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + userToken);

        SpaceDto spaceDto = SpaceDto.builder()
                .name("Test Space")
                .dimensions("100x200")
//                .mapId(mapId)
                .build();

        HttpEntity<SpaceDto> request = new HttpEntity<>(spaceDto, headers);

        //act
        ResponseEntity<SpaceDto> spaceResponse = restTemplate.postForEntity(url, request, SpaceDto.class);
        assertEquals(HttpStatus.CREATED, spaceResponse.getStatusCode(), "Expected a CREATED status for valid token");
        assertNotNull(spaceResponse.getBody(), "Space returned null");
        assertNotNull(spaceResponse.getBody().getId(), "Space returned null id");
        spaceId = spaceResponse.getBody().getId();
    }

    @Order(3)
    @Test
    @DisplayName("User is not able to create a space without mapId and dimensions (empty space)")
    void userIsNotAbleToCreateSpaceWithoutMapIdAndDimensions() {
        //arrange
        String url = "http://localhost:" + serverPort + "/api/v1/space";

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + userToken);

        SpaceDto spaceDto = SpaceDto.builder()
                .name("Test Space")
//                .dimensions("100x200")
//                .mapId(mapId)
                .build();

        HttpEntity<SpaceDto> request = new HttpEntity<>(spaceDto, headers);

        //act
        ResponseEntity<SpaceDto> spaceResponse = restTemplate.postForEntity(url, request, SpaceDto.class);

        // Assert that the response status is BAD_REQUEST (400)
        assertEquals(HttpStatus.BAD_REQUEST, spaceResponse.getStatusCode(), "Expected a BAD_REQUEST status for valid token");
        assertNull(spaceResponse.getBody(), "Space did not return null");
    }

    @Order(4)
    @Test
    @DisplayName("User is not able to delete a space that doesnt exist")
    void userIsNotAbleToDeleteASpaceThatDoesntExist() {
        //arrange
        String url = "http://localhost:" + serverPort + "/api/v1/space";

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + userToken);

        SpaceDto spaceDto = SpaceDto.builder()
                .id("some random bullshit")
//                .name("Test Space")
//                .dimensions("100x200")
//                .mapId(mapId)
                .build();

        HttpEntity<SpaceDto> request = new HttpEntity<>(spaceDto, headers);

        //act
        ResponseEntity<SpaceDto> spaceResponse = restTemplate.exchange(url, HttpMethod.DELETE, request, SpaceDto.class);

        // Assert that the response status is BAD_REQUEST (400)
        assertEquals(HttpStatus.BAD_REQUEST, spaceResponse.getStatusCode(), "Expected a BAD_REQUEST status for valid token");
        assertNull(spaceResponse.getBody(), "Space did not return null");
    }

    @Order(5)
    @Test
    @DisplayName("User is not able to delete a space")
    void userIsNotAbleToDeleteASpace() {
        //arrange
        String url = "http://localhost:" + serverPort + "/api/v1/space";

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + userToken);

        SpaceDto spaceDto = SpaceDto.builder()
                .id(spaceId)
//                .name("Test Space")
//                .dimensions("100x200")
//                .mapId(mapId)
                .build();

        HttpEntity<SpaceDto> request = new HttpEntity<>(spaceDto, headers);

        //act
        ResponseEntity<SpaceDto> spaceResponse = restTemplate.exchange(url, HttpMethod.DELETE, request, SpaceDto.class);

        // Assert that the response status is BAD_REQUEST (400)
        assertEquals(HttpStatus.OK, spaceResponse.getStatusCode(), "Expected a OK status for valid token");
    }

    @Order(6)
    @Test
    @DisplayName("User is not able to delete a space not owned by him")
    void userIsNotAbleToDeleteASpaceThatIsNotHis() {
        //arrange
        // setup another space with another user
        String user2Token = signUp("spacecontrolleruser2", Role.ROLE_USER);
        String Spaceurl = "http://localhost:" + serverPort + "/api/v1/space";

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + user2Token);

        SpaceDto spaceDto = SpaceDto.builder()
                .name("Test Space")
                .dimensions("100x200")
                .build();

        HttpEntity<SpaceDto> request = new HttpEntity<>(spaceDto, headers);
        ResponseEntity<SpaceDto> spaceResponse = restTemplate.postForEntity(Spaceurl, request, SpaceDto.class);
        String space2Id = spaceResponse.getBody().getId();
        // setup dto to try delete
        String deleteurl = "http://localhost:" + serverPort + "/api/v1/space";
        headers.add("Authorization", "Bearer " + userToken);
        SpaceDto space2Dto = SpaceDto.builder()
                .id(space2Id)
                .build();
        HttpEntity<SpaceDto> deleteRequest = new HttpEntity<>(space2Dto, headers);

        //act
        ResponseEntity<SpaceDto> spaceResponse2 = restTemplate.exchange(
                deleteurl, HttpMethod.DELETE, deleteRequest, SpaceDto.class);

        // Assert that the response status is BAD_REQUEST (400)
        assertEquals(HttpStatus.BAD_REQUEST, spaceResponse2.getStatusCode(), "Expected a BAD_REQUEST status for valid token");
        assertNull(spaceResponse2.getBody(), "Space did not return null");
    }

    private String signUp(String username, Role role){
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(username);
        loginDto.setPassword("password");
        loginDto.setRole(role);
        String signupUrl = "http://localhost:" + serverPort + "/auth/signup";
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginDto> signupRequest = new HttpEntity<>(loginDto, headers);
        ResponseEntity<AuthResponse> signupResponse = restTemplate.postForEntity(signupUrl, signupRequest, AuthResponse.class);
        AuthResponse authResponse = signupResponse.getBody();
        assert authResponse != null : "Signup failed, response is null.";
        return authResponse.getJwt();
    }

    @Order(7)
    @Test
    @DisplayName("Admin Should not have any spaces")
    void adminShouldNotHaveAnySpaces() {
        //arrange
        String Spaceurl = "http://localhost:" + serverPort + "/api/v1/space/all";

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + adminToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        //act
        ResponseEntity<List<SpaceDto>> spaceResponse = restTemplate.exchange(
                Spaceurl,HttpMethod.GET, request, new ParameterizedTypeReference<List<SpaceDto>>() {});

        assertEquals(HttpStatus.OK, spaceResponse.getStatusCode(), "Expected a OK status for valid token");
        assertNotNull(spaceResponse.getBody(), "Space returned null");
        assertEquals(spaceResponse.getBody().size(), 0, "Space did not return empty list");
    }

    @Order(8)
    @Test
    @DisplayName("Admin Should have one space")
    void adminShouldHaveOneSpace() {
        //arrange
        String url = "http://localhost:" + serverPort + "/api/v1/space";
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + adminToken);
        SpaceDto spaceDto = SpaceDto.builder()
                .name("Test Space")
                .dimensions("100x200")
                .mapId(mapId)
                .build();
        HttpEntity<SpaceDto> spacerequest = new HttpEntity<>(spaceDto, headers);
        ResponseEntity<SpaceDto> spacedto = restTemplate.postForEntity(url, spacerequest, SpaceDto.class);

        String Spaceurl = "http://localhost:" + serverPort + "/api/v1/space/all";
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + adminToken);
        HttpEntity<String> request = new HttpEntity<>(headers);
        //act
        ResponseEntity<List<SpaceDto>> spaceResponse = restTemplate.exchange(
                Spaceurl,HttpMethod.GET, request, new ParameterizedTypeReference<List<SpaceDto>>() {});

        assertEquals(HttpStatus.OK, spaceResponse.getStatusCode(), "Expected a OK status for valid token");
        assertNull(spaceResponse.getBody(), "Space did not return null");
        assertEquals(1, spaceResponse.getBody().size(), "There should be only one space");
        assertEquals(spacedto.getBody().getId(), spaceResponse.getBody().get(0).getId(), "Space did not return correct id");
    }
}