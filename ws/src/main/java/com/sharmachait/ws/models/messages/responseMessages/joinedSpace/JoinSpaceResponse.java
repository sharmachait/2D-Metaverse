package com.sharmachait.ws.models.messages.responseMessages.joinedSpace;

import com.sharmachait.ws.models.messages.MessageType;
import lombok.*;

@EqualsAndHashCode
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JoinSpaceResponse {
  private MessageType type;
  private JoinSpaceResponsePayload payload;
}
