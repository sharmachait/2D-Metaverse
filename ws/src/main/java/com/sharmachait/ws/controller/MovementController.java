package com.sharmachait.ws.controller;

import com.sharmachait.ws.models.messages.requestMessages.MovementMessageDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Controller
public class MovementController {
    @MessageMapping("/user/movement")//app/user/movement
    @SendTo("/topic/movement")
    public MovementMessageDto sendMovementMessage(@Payload MovementMessageDto movementMessageDto) {
        return movementMessageDto;
    }
}
