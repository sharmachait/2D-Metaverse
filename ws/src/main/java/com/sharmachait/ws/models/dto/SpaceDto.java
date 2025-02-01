package com.sharmachait.ws.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SpaceDto {
  private String id;
  private String name;
  private String dimensions;
  private String thumbnail;
  private String ownerId;
  private String mapId;
  List<SpaceElementDto> elements;

  @Override
  public int hashCode() {
    return Objects.hash(id); // only use the ID, not any collections
  }

  @Override
  public String toString() {
    return id;
  }
}