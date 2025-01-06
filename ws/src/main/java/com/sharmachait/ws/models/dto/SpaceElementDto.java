package com.sharmachait.ws.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceElementDto {
    private String id;
    private String elementId;
    private String spaceId;
    private String imageUrl;
    private boolean isStatic;
    private int x;
    private int y;
    @Override
    public int hashCode() {
        return Objects.hash(id); // only use the ID, not any collections
    }

    @Override
    public String toString() {
        return id;
    }
}
