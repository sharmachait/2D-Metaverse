package com.sharmachait.ws.repository;

import com.sharmachait.ws.models.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    Optional<ChatRoom> findBySenderAndRecipient(String sender, String recipient);
}
