package com.sharmachait.ws.controller;

import com.sharmachait.ws.models.messages.requestMessages.movement.MovementRequest;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MovementController {

  @MessageMapping("/move") // app/move
  @SendTo("/topic/movement")
  public MovementRequest sendMovementMessage(@Payload MovementRequest movementRequest) {
    return movementRequest;
  }
}
