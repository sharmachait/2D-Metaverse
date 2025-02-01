package com.sharmachait.ws.models.messages.responseMessages.leaveSpace;

import com.sharmachait.ws.models.messages.MessageType;
import lombok.*;

@EqualsAndHashCode
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveSpaceResponse {
  private MessageType type;
  private LeaveSpaceResponsePayload payload;
}
