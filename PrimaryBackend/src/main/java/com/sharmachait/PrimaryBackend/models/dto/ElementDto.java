package com.sharmachait.PrimaryBackend.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ElementDto {
    private String id;
    private String imageUrl;
    private int height;
    private int width;
    private Boolean isStatic;
}
