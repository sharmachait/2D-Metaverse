package com.sharmachait.PrimaryBackend.models.dto;

import lombok.Data;

@Data
public class SpaceElementDto {
    private String id;
    private String imageUrl;
    private boolean isStatic;
    private int x;
    private int y;
}
