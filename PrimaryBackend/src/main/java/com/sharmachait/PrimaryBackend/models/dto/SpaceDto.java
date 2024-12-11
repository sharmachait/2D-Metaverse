package com.sharmachait.PrimaryBackend.models.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpaceDto {
    private String id;
    private String name;
    private String dimensions;
    private String mapId;
}
