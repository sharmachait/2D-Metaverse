package com.sharmachait.ws.models.messages.responseMessages.joinedSpace;

import com.sharmachait.ws.models.messages.Payload;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class JoinSpaceResponsePayload extends Payload {
    private List<UserSpawn> users;
    private int x;
    private int y;
}
