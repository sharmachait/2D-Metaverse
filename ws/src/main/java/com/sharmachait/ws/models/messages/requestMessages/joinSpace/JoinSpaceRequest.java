package com.sharmachait.ws.models.messages.requestMessages.joinSpace;

import com.sharmachait.ws.models.messages.Message;
import com.sharmachait.ws.models.messages.MessageType;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class JoinSpaceRequest extends Message {
}
