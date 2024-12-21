package com.sharmachait.ws.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameMapDto {
    private String id;
    private String thumbnail;
    private String dimensions;
    private List<MapElementDto> mapElements;
}