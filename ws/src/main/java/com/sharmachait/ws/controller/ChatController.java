package com.sharmachait.ws.controller;

import com.sharmachait.ws.models.dto.ChatMessageEntityDto;
import com.sharmachait.ws.models.entity.ChatMessageEntity;
import com.sharmachait.ws.models.messages.ChatMessage.ChatMessage;
import com.sharmachait.ws.models.messages.MessageType;
import com.sharmachait.ws.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JdbcTemplate jdbc;

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessageEntity>> findChatMessages(
            @PathVariable("senderId") String senderId,
            @PathVariable("recipientId") String recipientId
    ){
        try {
            List<ChatMessageEntity> chatMessageEntities = chatMessageService.getChatMessages(senderId,recipientId);
            return ResponseEntity.ok(chatMessageEntities);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @MessageMapping("/chat")// /app/chat
    public void processMessage(
            @Payload ChatMessage chatMessage
    ){
        if(chatMessage.getType()!= MessageType.CHAT){
            return;
        }
        ChatMessageEntity chatMessageEntity = new ChatMessageEntity();
        chatMessageEntity.setSender(chatMessage.getSender());
        chatMessageEntity.setRecipient(chatMessage.getRecipient());
        chatMessageEntity.setContent(chatMessage.getPayload().getMessage());
        chatMessageEntity.setDate(Date.from(Instant.now()));

        try{
            ChatMessageEntity savedMessage = chatMessageService.save(chatMessageEntity);
            String recipient = chatMessage.getRecipient();
            ChatMessageEntityDto dto = ChatMessageEntityDto.builder()
                    .date(savedMessage.getDate())
                    .id(savedMessage.getId())
                    .chatId(savedMessage.getChatId())
                    .content(savedMessage.getContent())
                    .recipient(recipient)
                    .sender(savedMessage.getSender())
                    .build();
            messagingTemplate.convertAndSendToUser(
                    recipient,
                    "/queue/messages",
                    dto); // user/recipient/queue/messages
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
