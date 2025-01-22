package com.sharmachait.ws.models.messages.responseMessages.joinedSpace;

import lombok.*;

@EqualsAndHashCode
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JoinSpaceResponsePayload {
  private String message;
  private String token;
  private String userId;
  private String spaceId;
  private String username;
  private int x;
  private int y;
}
