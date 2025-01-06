package com.sharmachait.ws.models.messages.responseMessages.movement;

import lombok.*;

@EqualsAndHashCode
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementResponsePayload {
    private int x;
    private int y;
    private String token;
    private String userId;
    private String spaceId;
}
