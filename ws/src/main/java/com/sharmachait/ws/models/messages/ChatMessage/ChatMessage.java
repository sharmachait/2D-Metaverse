package com.sharmachait.ws.models.messages.ChatMessage;


import com.sharmachait.ws.models.messages.MessageType;
import com.sharmachait.ws.models.messages.requestMessages.joinSpace.JoinSpaceRequestPayload;
import lombok.*;

import java.util.Date;


@EqualsAndHashCode
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private MessageType type;
    private String sender;
    private String recipient;
    private String chatId; //sender__recipient
    private String spaceId;
    private ChatMessagePayload payload;
}
