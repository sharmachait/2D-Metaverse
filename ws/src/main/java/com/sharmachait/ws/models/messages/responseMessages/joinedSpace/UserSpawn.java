package com.sharmachait.ws.models.messages.responseMessages.joinedSpace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserSpawn {
    private String userId;
    private int x;
    private int y;
}
