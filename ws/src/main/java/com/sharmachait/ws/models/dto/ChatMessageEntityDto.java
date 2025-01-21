package com.sharmachait.ws.models.dto;

import com.sharmachait.ws.models.entity.ChatRoom;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ChatMessageEntityDto {
  private String id;
  private String sender;
  private String recipient;
  private String content;
  private String chatId;
  private Date date;

  @Override
  public int hashCode() {
    return Objects.hash(id); // only use the ID, not any collections
  }

  @Override
  public String toString() {
    return id;
  }
}
