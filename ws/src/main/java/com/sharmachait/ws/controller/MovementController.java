package com.sharmachait.ws.controller;

import com.sharmachait.ws.models.messages.requestMessages.movement.MovementMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Controller
public class MovementController {
    @MessageMapping("/move")//app/movement
    @SendTo("/topic/movement")
    public MovementMessage sendMovementMessage(@Payload MovementMessage movementMessage) {
        return movementMessage;
    }
}
