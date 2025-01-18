package com.sharmachait.ws.models.messages;

import lombok.*;

@EqualsAndHashCode
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ping {
    private MessageType type;
    private PingPayload payload;
}
