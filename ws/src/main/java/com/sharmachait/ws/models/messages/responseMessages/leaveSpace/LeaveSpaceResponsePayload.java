package com.sharmachait.ws.models.messages.responseMessages.leaveSpace;

import lombok.*;

@EqualsAndHashCode
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveSpaceResponsePayload {
  private String token;
  private String userId;
  private String email;
  private String spaceId;
}
