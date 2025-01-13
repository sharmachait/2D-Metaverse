package com.sharmachait.ws.controller;

import com.sharmachait.ws.models.messages.MessageType;
import com.sharmachait.ws.models.messages.requestMessages.joinSpace.JoinSpaceRequest;
import com.sharmachait.ws.service.SpaceService;
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

        if(request.getType().equals(MessageType.LEAVE)){
            spaceService.leave(request, headerAccessor);
        }
        if(!request.getType().equals(MessageType.JOIN)){
            spaceService.join(request, headerAccessor);
        }

        throw new Exception();

    }

}
