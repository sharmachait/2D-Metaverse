package com.sharmachait.ws.models.messages.responseMessages.movement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementResponsePayload {
    private String spaceId;
    private String userId;
    private int x;
    private int y;
}
