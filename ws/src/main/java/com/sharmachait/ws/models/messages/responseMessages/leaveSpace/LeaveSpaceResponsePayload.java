package com.sharmachait.ws.models.messages.responseMessages.leaveSpace;

import com.sharmachait.ws.models.messages.Payload;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveSpaceResponsePayload extends Payload {
}
