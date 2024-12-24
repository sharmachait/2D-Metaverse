package com.sharmachait.ws.models.messages.responseMessages.leaveSpace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveSpaceResponsePayload {
    private String userId;
    private String spaceId;
}
