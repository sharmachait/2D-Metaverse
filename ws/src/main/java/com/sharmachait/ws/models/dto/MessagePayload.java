package com.sharmachait.ws.models.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessagePayload {
    private String spaceId;
    private String senderId;
    private String token;
}
