package com.sharmachait.ws.config;

import com.sharmachait.ws.models.entity.Status;
import com.sharmachait.ws.models.entity.User;
import com.sharmachait.ws.models.messages.MessageType;
import com.sharmachait.ws.models.messages.responseMessages.leaveSpace.LeaveSpaceResponse;
import com.sharmachait.ws.models.messages.responseMessages.leaveSpace.LeaveSpaceResponsePayload;
import com.sharmachait.ws.repository.UserRespository;
import com.sharmachait.ws.service.UserService;
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

    private final SimpMessageSendingOperations messagingTemplate;
    private final UserRespository userRespository;
    private final UserService userService;

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        try {
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
            String userSpace = (String) headerAccessor.getSessionAttributes().get("user___space");

            if (userSpace == null) {
                log.warn("No user___space attribute found in session");
                return;
            }

            String[] parts = userSpace.split("___");
            if (parts.length != 2) {
                log.warn("Invalid user___space format: {}", userSpace);
                return;
            }

            String email = parts[0];
            String spaceId = parts[1];


            User user = userService.Disconnect(email, spaceId);

            LeaveSpaceResponse response = LeaveSpaceResponse.builder()
                    .type(MessageType.USER_LEFT)
                    .payload(LeaveSpaceResponsePayload.builder()
                            .spaceId(spaceId)
                            .email(email)
                            .userId(user.getId())
                            .build())
                    .build();

            messagingTemplate.convertAndSend("/topic/space/" + spaceId, response);

            log.debug("User {} left space {}", user.getId(), spaceId);
        } catch (Exception e) {
            log.error("Error handling WebSocket disconnect event", e);
        }
    }
}
