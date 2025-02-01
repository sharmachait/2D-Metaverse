package com.sharmachait.ws.models.messages.ChatMessage;

import lombok.*;

@EqualsAndHashCode
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessagePayload {
  private String token;
  private String message;
}
