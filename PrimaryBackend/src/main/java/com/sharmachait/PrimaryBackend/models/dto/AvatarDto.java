package com.sharmachait.PrimaryBackend.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AvatarDto {
    private String id;
    private String imageUrl;
    private String name;
    @Override
    public int hashCode() {
        return Objects.hash(id); // only use the ID, not any collections
    }

    @Override
    public String toString() {
        return id;
    }
}
