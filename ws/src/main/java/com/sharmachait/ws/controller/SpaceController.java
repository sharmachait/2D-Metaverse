package com.sharmachait.ws.controller;

import com.sharmachait.ws.config.jwt.JwtProvider;
import com.sharmachait.ws.models.messages.MessageType;
import com.sharmachait.ws.models.messages.requestMessages.joinSpace.JoinSpaceRequest;
import com.sharmachait.ws.models.messages.responseMessages.joinedSpace.JoinSpaceResponse;
import com.sharmachait.ws.models.messages.responseMessages.joinedSpace.JoinSpaceResponsePayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpaceController {

    @Autowired
    SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/space")
    public JoinSpaceResponse joinSpace(@Payload JoinSpaceRequest request,
                                       SimpMessageHeaderAccessor headerAccessor) {
        if(!request.getType().equals(MessageType.JOIN)){
            return JoinSpaceResponse.builder()
                    .type(MessageType.BAD_REQUEST)
                    .payload(JoinSpaceResponsePayload.builder()
                            .message("wrong type sent to wrong endpoint")
                            .build())
                    .build();
        }


        String spaceId = request.getPayload().getSpaceId();
        String token = request.getPayload().getToken();
        String userId = JwtProvider.getIdFromToken(token);
        headerAccessor.getSessionAttributes().put("user___space",userId+"___"+spaceId );
        messagingTemplate.convertAndSend("/topic/space/" + spaceId, request);


        return JoinSpaceResponse.builder().build();
    }
}
