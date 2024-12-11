package com.sharmachait.PrimaryBackend.models.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MapElementDto {
    private String elementId;
    private int x;
    private int y;
}
