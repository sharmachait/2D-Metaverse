package com.sharmachait.ws.models.messages.responseMessages.movement;

import com.sharmachait.ws.models.messages.MessageType;
import com.sharmachait.ws.models.messages.requestMessages.movement.MovementRequestPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementResponse {
    private MessageType type;
    private MovementResponsePayload payload;
}
