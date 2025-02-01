package com.sharmachait.ws.models.messages.responseMessages.joinedSpace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Spawn {
  private int x;
  private int y;
}
