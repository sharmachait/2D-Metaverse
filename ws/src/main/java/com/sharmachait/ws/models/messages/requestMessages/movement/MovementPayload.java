package com.sharmachait.ws.models.messages.requestMessages.movement;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MovementPayload {
    private String token;
    private String userId;
    private int x;
    private int y;
}
