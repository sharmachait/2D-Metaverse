package com.sharmachait.ws.models.messages.requestMessages.joinSpace;

import lombok.*;

@EqualsAndHashCode
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JoinSpaceRequestPayload {
  private String senderId;
  private String token;
  private String userId;
  private String spaceId;
}
