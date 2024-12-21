package com.sharmachait.ws.models.messages.responseMessages.joinedSpace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JoinedSpacePayload {
    private List<Integer> users;
    private Spawn spawn;
}
