package com.sharmachait.PrimaryBackend.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SpaceDto {
    private String id;
    private String name;
    private String dimensions;
    private String mapId;
    List<SpaceElementDto> elements;
}
