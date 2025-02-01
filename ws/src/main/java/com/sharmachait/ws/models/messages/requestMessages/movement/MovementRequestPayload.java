package com.sharmachait.ws.models.messages.requestMessages.movement;

import lombok.*;

@EqualsAndHashCode
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementRequestPayload {
  private String token;
  private String userId;
  private String spaceId;
  private int x;
  private int y;
}
