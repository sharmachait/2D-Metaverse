package com.sharmachait.ws.models.messages.requestMessages.joinSpace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JoinSpacePayload {
    private String spaceId;
    private String senderId;
    private String token;
}
