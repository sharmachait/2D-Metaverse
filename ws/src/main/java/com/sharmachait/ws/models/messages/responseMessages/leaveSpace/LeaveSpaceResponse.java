package com.sharmachait.ws.models.messages.responseMessages.leaveSpace;

import com.sharmachait.ws.models.messages.MessageType;
import com.sharmachait.ws.models.messages.responseMessages.movement.MovementResponsePayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveSpaceResponse {
    private MessageType type;
    private LeaveSpaceResponsePayload payload;
}
