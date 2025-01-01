package com.sharmachait.PrimaryBackend.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpaceElementDto {
    private String id;
    private String elementId;
    private String spaceId;
    private boolean isStatic;
    private int x;
    private int y;

}
