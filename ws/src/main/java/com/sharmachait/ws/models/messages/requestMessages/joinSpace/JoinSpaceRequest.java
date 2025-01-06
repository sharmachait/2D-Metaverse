package com.sharmachait.ws.models.messages.requestMessages.joinSpace;


import com.sharmachait.ws.models.messages.MessageType;
import lombok.*;


@EqualsAndHashCode
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JoinSpaceRequest{
    private MessageType type;
    private JoinSpaceRequestPayload payload;
}
