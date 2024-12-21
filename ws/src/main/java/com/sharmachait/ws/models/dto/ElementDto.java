package com.sharmachait.ws.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElementDto {
    private String id;
    private String imageUrl;
    private int height;
    private int width;
    private Boolean isStatic;
}