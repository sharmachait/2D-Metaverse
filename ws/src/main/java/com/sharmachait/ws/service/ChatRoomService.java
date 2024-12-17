package com.sharmachait.ws.service;

import com.sharmachait.ws.model.ChatRoom;
import com.sharmachait.ws.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    @Autowired
    private final ChatRoomRepository chatRoomRepository;

    public String getChatRoomId(
            Long senderId,
            Long recipientId,
            boolean createNewRoomIfNotExist) throws NoSuchElementException {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId);
        if (chatRoom.isPresent()) {
            return chatRoom.get().getChatId();
        }
        else {
            if(createNewRoomIfNotExist) {
                return createChat(senderId,recipientId);
            }else{
                throw new NoSuchElementException();
            }
        }
    }
    public String createChat(Long senderId, Long recipientId) {
        String chatId = senderId.toString()+"_"+recipientId.toString();
        String chatIdReverse = recipientId.toString()+"_"+senderId.toString();

        ChatRoom senderRecipient = ChatRoom.builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();
        chatRoomRepository.save(senderRecipient);

        ChatRoom recipientSender = ChatRoom.builder()
                .chatId(chatIdReverse)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();
        chatRoomRepository.save(recipientSender);

        return chatId;
    }
}
