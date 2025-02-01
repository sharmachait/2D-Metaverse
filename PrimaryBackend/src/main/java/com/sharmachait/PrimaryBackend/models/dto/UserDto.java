package com.sharmachait.PrimaryBackend.models.dto;

import com.sharmachait.PrimaryBackend.models.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
  private String id;
  private String avatarId;
  private Role role;
  private String username;
  private Set<SpaceDto> spaces;

  @Override
  public int hashCode() {
    return Objects.hash(id); // only use the ID, not any collections
  }

  @Override
  public String toString() {
    return id;
  }
}
