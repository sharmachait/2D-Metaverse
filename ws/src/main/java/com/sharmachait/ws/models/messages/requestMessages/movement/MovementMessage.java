package com.sharmachait.ws.models.messages.requestMessages.movement;

import com.sharmachait.ws.models.messages.MessageType;
import com.sharmachait.ws.models.messages.requestMessages.joinSpace.JoinSpacePayload;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MovementMessage {
    private MessageType type;
    private MovementPayload payload;

}
