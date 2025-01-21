package com.sharmachait.ws.repository;

import com.sharmachait.ws.models.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, String> {
  List<ChatMessageEntity> findByChatRoom_Id(String chatRoomId);

  List<ChatMessageEntity> findByChatId(String chatId);
}
