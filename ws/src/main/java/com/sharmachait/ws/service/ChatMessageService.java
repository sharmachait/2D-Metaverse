package com.sharmachait.ws.service;

import com.sharmachait.ws.models.entity.ChatMessage;
import com.sharmachait.ws.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class ChatMessageService {
    @Autowired
    private final ChatMessageRepository chatMessageRepository;
    @Autowired
    private final ChatRoomService chatRoomService;

    public ChatMessage save(ChatMessage chatMessage) throws NoSuchElementException {
        try{
            String chatId = chatRoomService.getChatRoomId(chatMessage.getSenderId(),chatMessage.getRecipientId(),true);
            chatMessage.setChatId(chatId);
            return chatMessageRepository.save(chatMessage);
        } catch (NoSuchElementException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ChatMessage> getChatMessages(Long senderId, Long recipientId) throws NoSuchElementException {
        try{
            String chatId = chatRoomService.getChatRoomId(senderId,recipientId,false);
            List<ChatMessage> chatMessages = chatMessageRepository.findByChatId(chatId);
            if(chatMessages==null){
                chatMessages = new ArrayList<>();
            }
            return chatMessages;
        }
        catch (NoSuchElementException e) {
            throw new RuntimeException(e);
        }
    }
}
