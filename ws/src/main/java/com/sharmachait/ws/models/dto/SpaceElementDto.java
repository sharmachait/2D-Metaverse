package com.sharmachait.ws.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceElementDto {
    private String id;
    private String imageUrl;
    private boolean isStatic;
    private int x;
    private int y;
}
