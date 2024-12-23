package com.sharmachait.PrimaryBackend.models.dto;

import lombok.Data;

import java.util.List;

@Data
public class GameMapDto {
    private String id;
    private String thumbnail;
    private String dimensions;
    private List<MapElementDto> mapElements;
}
