package com.sharmachait.ws.models.messages.requestMessages.joinSpace;

import com.sharmachait.ws.models.messages.MessageType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JoinSpaceMessage {
    private MessageType type;
    private JoinSpacePayload payload;
}
