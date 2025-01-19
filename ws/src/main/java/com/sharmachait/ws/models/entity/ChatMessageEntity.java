package com.sharmachait.ws.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

@Data
@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ChatMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String sender;
    private String recipient;
    private String content;
    private String chatId;
    private Date date;
    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false) // Foreign key column
    private ChatRoom chatRoom;

    @Override
    public int hashCode() {
        return Objects.hash(id); // only use the ID, not any collections
    }
    @Override
    public boolean equals(Object o) {
        return Objects.equals(this, o);
    }

    @Override
    public String toString() {
        return id;
    }
}
