package com.sharmachait.ws.models.messages.requestMessages.joinSpace;

import com.sharmachait.ws.models.messages.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JoinSpaceRequest {
    private MessageType type;
    private JoinSpaceRequestPayload payload;
}
