package com.sharmachait.ws.models.messages.requestMessages.movement;

import com.sharmachait.ws.models.messages.Message;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementRequest extends Message {
}
