package com.sharmachait.ws.models.messages.requestMessages.movement;

import com.sharmachait.ws.models.messages.Payload;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementRequestPayload extends Payload {
    private int x;
    private int y;
}
