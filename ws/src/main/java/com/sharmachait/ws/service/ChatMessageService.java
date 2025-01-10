package com.sharmachait.ws.service;

import com.sharmachait.ws.models.entity.ChatMessageEntity;
import com.sharmachait.ws.models.entity.ChatRoom;
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

    public ChatMessageEntity save(ChatMessageEntity chatMessageEntity) throws Exception {
        try{
            ChatRoom chatRoom = chatRoomService.getChatRoom(chatMessageEntity.getSender(), chatMessageEntity.getRecipient(),true);
            chatMessageEntity.setChatRoom(chatRoom);
            chatMessageEntity.setChatId(chatRoom.getChatId());
            return chatMessageRepository.save(chatMessageEntity);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public List<ChatMessageEntity> getChatMessages(String senderId, String recipientId) throws NoSuchElementException {
        try{
            ChatRoom chatRoom = chatRoomService.getChatRoom(senderId,recipientId,false);
            List<ChatMessageEntity> chatMessageEntities = chatMessageRepository.findByChatRoom_Id(chatRoom.getId());
            if(chatMessageEntities ==null){
                chatMessageEntities = new ArrayList<>();
            }
            return chatMessageEntities;
        }
        catch (NoSuchElementException e) {
            throw new RuntimeException(e);
        }
    }
}
