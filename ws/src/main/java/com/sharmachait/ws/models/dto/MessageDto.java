package com.sharmachait.ws.models.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageDto {
    private MessageType type;
    private MessagePayload payload;
}
