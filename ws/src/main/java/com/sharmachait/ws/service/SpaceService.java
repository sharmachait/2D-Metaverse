package com.sharmachait.ws.service;

import com.sharmachait.ws.config.jwt.JwtProvider;
import com.sharmachait.ws.models.dto.SpaceDto;
import com.sharmachait.ws.models.dto.SpaceElementDto;
import com.sharmachait.ws.models.dto.UserDto;
import com.sharmachait.ws.models.entity.Role;
import com.sharmachait.ws.models.entity.Status;
import com.sharmachait.ws.models.entity.User;
import com.sharmachait.ws.models.entity.metaverse.Space;
import com.sharmachait.ws.models.messages.MessageType;
import com.sharmachait.ws.models.messages.Ping;
import com.sharmachait.ws.models.messages.PingPayload;
import com.sharmachait.ws.models.messages.requestMessages.joinSpace.JoinSpaceRequest;
import com.sharmachait.ws.models.messages.requestMessages.movement.MovementRequest;
import com.sharmachait.ws.models.messages.responseMessages.joinedSpace.JoinSpaceResponse;
import com.sharmachait.ws.models.messages.responseMessages.joinedSpace.JoinSpaceResponsePayload;
import com.sharmachait.ws.models.messages.responseMessages.joinedSpace.UserSpawn;
import com.sharmachait.ws.models.messages.responseMessages.leaveSpace.LeaveSpaceResponse;
import com.sharmachait.ws.models.messages.responseMessages.leaveSpace.LeaveSpaceResponsePayload;
import com.sharmachait.ws.models.messages.responseMessages.movement.MovementResponse;
import com.sharmachait.ws.models.messages.responseMessages.movement.MovementResponsePayload;
import com.sharmachait.ws.repository.UserRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SpaceService {
  @Autowired
  SimpMessageSendingOperations messagingTemplate;
  @Autowired
  private UserService userService;
  @Autowired
  private UserRespository userRespository;
  @Autowired
  @Qualifier("metaverseJdbcTemplate")
  private JdbcTemplate jdbcTemplate;

  public Space getSpaceBySpaceId(String spaceId) throws Exception {
    String SPACE_QUERY = """
            SELECT *
            FROM space s
            WHERE s.id = ?
        """;
    String SPACE_ELEMENTS_QUERY = """
            SELECT id
            FROM space_element
            WHERE space_id = ?
        """;
    try {
      Space space = jdbcTemplate.query(SPACE_QUERY,
          new Object[] { spaceId },
          (rs, rowNum) -> {
            Space space2 = Space.builder()
                .id(rs.getString("id"))
                .name(rs.getString("name"))
                .width(rs.getInt("width"))
                .height(rs.getInt("height"))
                .thumbnail(rs.getString("thumbnail"))
                .ownerId(rs.getString("owner_id"))
                .gameMapId(rs.getString("map_id"))
                .spaceElementIds(new ArrayList<>()) // Initialize empty list
                .build();
            return space2;
          }).stream().findFirst().orElseThrow(() -> new RuntimeException("Space not found with id: " + spaceId));
      List<String> elementIds = jdbcTemplate.query(SPACE_ELEMENTS_QUERY,
          new Object[] { spaceId },
          (rs, rowNum) -> rs.getString("id"));

      space.setSpaceElementIds(elementIds);
      return space;

    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw e;
    }
  }

  public List<SpaceElementDto> getSpaceElementBySpaceId(String spaceId) throws Exception {

    String SPACE_ELEMENTS_QUERY = """
            SELECT *
            FROM space_element
            WHERE space_id = ?
        """;
    try {
      List<SpaceElementDto> spaceElementDto = jdbcTemplate.query(
          SPACE_ELEMENTS_QUERY,
          new Object[] { spaceId },
          (rs, rowNum) -> {
            SpaceElementDto sed = new SpaceElementDto();
            sed.setSpaceId(rs.getString("space_id"));
            sed.setId(rs.getString("id"));
            sed.setX(rs.getInt("x"));
            sed.setY(rs.getInt("y"));
            sed.setElementId(rs.getString("element_id"));
            return sed;
          });

      return spaceElementDto;

    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw e;
    }
  }

  public void pong(Ping request, SimpMessageHeaderAccessor headerAccessor) {
    messagingTemplate.convertAndSendToUser(
        request.getPayload().getUserFor(),
        "/queue/messages",
        request);
  }

  public void pingUsersFor(String spaceId, String userEmail) throws Exception {
    messagingTemplate.convertAndSend("/topic/space/" + spaceId, Ping.builder()
        .type(MessageType.PING)
        .payload(PingPayload.builder()
            .userFor(userEmail)
            .build())
        .build());
  }

  public int[] generateSpawn(List<SpaceElementDto> l, Space space) {
    Set<List<Integer>> taken = new HashSet<>();
    for (SpaceElementDto dto : l) {
      if (dto.isStatic()) {
        List<Integer> ordinates = List.of(dto.getX(), dto.getY());
        taken.add(ordinates);
      }
    }
    if (taken.isEmpty()) {
      return new int[] { 0, 0 };
    }
    for (int x = 0; x < space.getWidth(); x++) {
      for (int y = 0; y < space.getHeight(); y++) {
        List<Integer> ordinates = List.of(x, y);
        if (!taken.contains(ordinates)) {
          return new int[] { x, y };
        }
      }
    }
    return new int[0];
  }

  public void join(JoinSpaceRequest request, SimpMessageHeaderAccessor headerAccessor) throws Exception {
    String token = request.getPayload().getToken();
    String userEmail = JwtProvider.getEmailFromToken(token);
    String spaceId = request.getPayload().getSpaceId();

    headerAccessor.getSessionAttributes().put("user___space", userEmail + "___" + spaceId);

    User user = User.builder()
        .status(Status.ONLINE)
        .role(Role.ROLE_USER)
        .spaceId(spaceId)
        .username(userEmail)
        .build();

    user = userRespository.save(user);
    request.getPayload().setUserId(user.getId());

    pingUsersFor(spaceId, userEmail);
    Thread.sleep(1000);
    Space space = getSpaceBySpaceId(spaceId);
    List<SpaceElementDto> spaceElementDtos = getSpaceElementBySpaceId(spaceId);
    int coor[] = generateSpawn(spaceElementDtos, space);
    JoinSpaceResponse response = JoinSpaceResponse.builder()
        .type(MessageType.SPACE_JOINED)
        .payload(JoinSpaceResponsePayload.builder()
            .x(coor[0])
            .y(coor[1])
            .username(userEmail)
            .spaceId(request.getPayload().getSpaceId())
            .userId(request.getPayload().getUserId())
            .build())
        .build();

    messagingTemplate.convertAndSend("/topic/space/" + spaceId, response);
  }

  public void move(MovementRequest request, SimpMessageHeaderAccessor headerAccessor) throws Exception {
    String token = request.getPayload().getToken();
    String userEmail = JwtProvider.getEmailFromToken(token);
    String spaceId = request.getPayload().getSpaceId();
    Space space = getSpaceBySpaceId(spaceId);
    int x = request.getPayload().getX();
    int y = request.getPayload().getY();
    if (!isObject(spaceId, x, y)) {
      if (x < 0 || x > space.getWidth() || y < 0 || y > space.getHeight()) {
        MovementResponse res = MovementResponse.builder()
            .type(MessageType.MOVE_REJECTED)
            .payload(MovementResponsePayload.builder()
                .spaceId(request.getPayload().getSpaceId())
                .x(request.getPayload().getX())
                .y(request.getPayload().getY())
                .userId(request.getPayload().getUserId())
                .build())
            .build();
        messagingTemplate.convertAndSend("/topic/space/" + spaceId, res);
      }
      MovementResponse res = MovementResponse.builder()
          .type(MessageType.MOVE)
          .payload(MovementResponsePayload.builder()
              .spaceId(request.getPayload().getSpaceId())
              .x(request.getPayload().getX())
              .y(request.getPayload().getY())
              .userId(request.getPayload().getUserId())
              .build())
          .build();
      messagingTemplate.convertAndSend("/topic/space/" + spaceId, res);
    } else {
      MovementResponse res = MovementResponse.builder()
          .type(MessageType.MOVE_REJECTED)
          .payload(MovementResponsePayload.builder()
              .spaceId(request.getPayload().getSpaceId())
              .x(request.getPayload().getX())
              .y(request.getPayload().getY())
              .userId(request.getPayload().getUserId())
              .build())
          .build();
      messagingTemplate.convertAndSend("/topic/space/" + spaceId, res);
    }

  }

  private boolean isObject(String spaceId, int x, int y) throws Exception {
    List<SpaceElementDto> l = getSpaceElementBySpaceId(spaceId);
    Set<List<Integer>> taken = new HashSet<>();
    for (SpaceElementDto dto : l) {
      if (dto.isStatic()) {
        List<Integer> ordinates = List.of(dto.getX(), dto.getY());
        taken.add(ordinates);
      }
    }
    if (taken.isEmpty()) {
      return false;
    }
    return taken.contains(List.of(x, y));
  }

  public void leave(JoinSpaceRequest request, SimpMessageHeaderAccessor headerAccessor) throws Exception {
    String token = request.getPayload().getToken();
    String userEmail = JwtProvider.getEmailFromToken(token);
    String spaceId = request.getPayload().getSpaceId();
    User user = userService.Disconnect(userEmail, spaceId);

    LeaveSpaceResponse response = LeaveSpaceResponse.builder()
        .type(MessageType.USER_LEFT)
        .payload(LeaveSpaceResponsePayload.builder()
            .spaceId(spaceId)
            .email(userEmail)
            .userId(user.getId())
            .build())
        .build();

    messagingTemplate.convertAndSend("/topic/space/" + spaceId, response);
  }
}
