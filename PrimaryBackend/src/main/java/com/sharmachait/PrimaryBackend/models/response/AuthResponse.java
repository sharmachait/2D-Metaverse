package com.sharmachait.PrimaryBackend.models.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthResponse {
  private String jwt;
  private Boolean status = false;
  private String message;
  private String session = null;
  private String userId;
}