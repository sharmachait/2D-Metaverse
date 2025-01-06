package com.sharmachait.ws.config;

import com.sharmachait.ws.models.messages.MessageType;
import com.sharmachait.ws.models.messages.responseMessages.leaveSpace.LeaveSpaceResponse;
import com.sharmachait.ws.models.messages.responseMessages.leaveSpace.LeaveSpaceResponsePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {
    @Autowired
    SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userSpace = (String) headerAccessor.getSessionAttributes().get("user___space");
        String userId = userSpace.split("___")[0];
        String spaceId = userSpace.split("___")[1];
        LeaveSpaceResponse response = LeaveSpaceResponse.builder()
                .type(MessageType.USER_LEFT)
                .payload(LeaveSpaceResponsePayload.builder()
                        .spaceId(spaceId)
                        .userId(userId)
                        .build())
                .build();
        messagingTemplate.convertAndSend("/topic/space/"+spaceId, response);
    }
}
