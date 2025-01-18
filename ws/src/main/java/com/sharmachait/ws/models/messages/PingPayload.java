package com.sharmachait.ws.models.messages;

import lombok.*;

@EqualsAndHashCode
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PingPayload {
    private String token;
    private String spaceId;
    private String userFor;
    private String userFrom;
    private int fromX;
    private int fromY;
}
