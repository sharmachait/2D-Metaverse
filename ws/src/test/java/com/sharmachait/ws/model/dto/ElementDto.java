package com.sharmachait.ws.model.dto;

import lombok.Data;

@Data
public class ElementDto {
    private String id;
    private String imageUrl;
    private int height;
    private int width;
    private Boolean isStatic;
}