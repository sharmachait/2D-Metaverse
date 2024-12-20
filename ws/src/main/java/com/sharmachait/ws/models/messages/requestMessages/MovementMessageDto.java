package com.sharmachait.ws.models.messages.requestMessages;

import lombok.Data;

@Data
public class MovementMessageDto {
    private String userId;
    private int x;
    private int y;
}
