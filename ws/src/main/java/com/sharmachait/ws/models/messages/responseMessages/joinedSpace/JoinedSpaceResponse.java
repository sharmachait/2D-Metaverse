package com.sharmachait.ws.models.messages.responseMessages.joinedSpace;

import com.sharmachait.ws.models.messages.MessageType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JoinedSpaceResponse {
    private MessageType type;
    private JoinedSpacePayload payload;
}
