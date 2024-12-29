package com.sharmachait.PrimaryBackend.models.dto;

import jakarta.validation.constraints.Pattern;
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
    @Pattern(
            regexp = "^[0-9]{1,3}x[0-9]{1,3}$",
            message = "Dimensions must match the pattern: ^[0-9]{1,3}x[0-9]{1,3}$"
    )
    private String name;
    private String dimensions;
    private List<MapElementDto> mapElements;
}
