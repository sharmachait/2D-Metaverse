package com.sharmachait.ws.models.messages.requestMessages.joinSpace;

import com.sharmachait.ws.models.messages.Payload;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class JoinSpaceRequestPayload extends Payload {
    private String senderId;
}
