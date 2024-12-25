package com.sharmachait.PrimaryBackend.models.dto;

import jakarta.validation.constraints.Pattern;
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
    @Pattern(
            regexp = "^[0-9]{1,3}x[0-9]{1,3}$",
            message = "Dimensions must match the pattern: ^[0-9]{1,3}x[0-9]{1,3}$"
    )
    private String dimensions;
    private String mapId;
    List<SpaceElementDto> elements;
}
