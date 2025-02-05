package com.sharmachait.ws.controller;

import com.sharmachait.ws.models.messages.MessageType;
import com.sharmachait.ws.models.messages.Ping;
import com.sharmachait.ws.models.messages.requestMessages.joinSpace.JoinSpaceRequest;
import com.sharmachait.ws.models.messages.requestMessages.movement.MovementRequest;
import com.sharmachait.ws.service.SpaceService;

import java.net.URI;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpaceController {

  @Autowired
  private SpaceService spaceService;

  @MessageMapping("/space")
  public void joinSpace(@Payload JoinSpaceRequest request,
      SimpMessageHeaderAccessor headerAccessor) throws Exception {
    // String uri = headerAccessor.getSessionAttributes().get("uri").toString();
    // URI parsedUri = new URI(uri);
    // String query = parsedUri.getQuery();

    // String spaceId = Arrays.stream(query.split("&"))
    // .filter(param -> param.startsWith("spaceId="))
    // .map(param -> param.split("=")[1])
    // .findFirst()
    // .orElse(request.getPayload().getSpaceId());

    // String token = Arrays.stream(query.split("&"))
    // .filter(param -> param.startsWith("token="))
    // .map(param -> param.split("=")[1])
    // .findFirst()
    // .orElse(request.getPayload().getToken());

    String spaceId = request.getPayload().getSpaceId();
    String token = request.getPayload().getToken();

    if (request.getType().equals(MessageType.LEAVE)) {
      spaceService.leave(request, headerAccessor);
    } else if (request.getType().equals(MessageType.JOIN)) {
      spaceService.join(request, headerAccessor, spaceId, token);
    } else {
      throw new Exception();
    }
  }

  @MessageMapping("/space/move")
  public void joinSpace(@Payload MovementRequest request,
      SimpMessageHeaderAccessor headerAccessor) throws Exception {

    if (request.getType().equals(MessageType.MOVE)) {
      spaceService.move(request, headerAccessor);
    }

    else {
      throw new Exception();
    }
  }

  @MessageMapping("/space/ping")
  public void ping(@Payload Ping request,
      SimpMessageHeaderAccessor headerAccessor) throws Exception {

    if (request.getType().equals(MessageType.PONG)) {
      spaceService.pong(request, headerAccessor);
    } else {
      throw new Exception();
    }
  }

}
