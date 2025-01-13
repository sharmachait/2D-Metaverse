package com.sharmachait.ws.models.messages.responseMessages.joinedSpace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSpawn {
    private String username;
    private int x;
    private int y;
}
