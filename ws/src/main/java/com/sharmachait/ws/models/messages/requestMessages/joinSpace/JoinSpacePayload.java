package com.sharmachait.ws.models.messages.requestMessages.joinSpace;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JoinSpacePayload {
    private String spaceId;
    private String senderId;
    private String token;
}
