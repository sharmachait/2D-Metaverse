package com.sharmachait.ws.models.messages.requestMessages.movement;

import com.sharmachait.ws.models.messages.MessageType;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementRequest {
  private MessageType type;
  private MovementRequestPayload payload;
}
