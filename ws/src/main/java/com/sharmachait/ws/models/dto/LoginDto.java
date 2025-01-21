package com.sharmachait.ws.models.dto;

import com.sharmachait.ws.models.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
  private String username;
  private String password;
  private Role role;

  @Override
  public int hashCode() {
    return Objects.hash(username + password + role.toString()); // only use the ID, not any collections
  }

  @Override
  public String toString() {
    return username + password + role.toString();
  }
}