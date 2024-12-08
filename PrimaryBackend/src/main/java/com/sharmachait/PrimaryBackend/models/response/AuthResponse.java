package com.sharmachait.PrimaryBackend.models.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String jwt;
    private boolean status = false;
    private String message;
    private String session = null;
}