package com.sharmachait.ws.models.messages.responseMessages.joinedSpace;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JoinedSpacePayload {
    private List<Integer> users;
    private Spawn spawn;
}
