package com.sharmachait.ws.model.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MapElementDto {
    private String elementId;
    private int x;
    private int y;
}
