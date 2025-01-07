package com.sharmachait.ws.models.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String senderId;
    private String recipientId;
    private String message;

    @Override
    public int hashCode() {
        return Objects.hash(id); // only use the ID, not any collections
    }

    @Override
    public String toString() {
        return id;
    }
}
