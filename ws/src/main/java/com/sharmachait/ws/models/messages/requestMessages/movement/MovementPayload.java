package com.sharmachait.ws.models.messages.requestMessages.movement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementPayload {
    private String token;
    private String userId;
    private int x;
    private int y;
}
