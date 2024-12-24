package com.sharmachait.ws.models.messages.responseMessages.joinedSpace;

import com.sharmachait.ws.models.messages.Message;
import com.sharmachait.ws.models.messages.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class JoinSpaceResponse extends Message {
}
