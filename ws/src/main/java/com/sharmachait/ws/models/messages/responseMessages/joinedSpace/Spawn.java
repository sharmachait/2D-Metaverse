package com.sharmachait.ws.models.messages.responseMessages.joinedSpace;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Spawn {
    private int x;
    private int y;
}
