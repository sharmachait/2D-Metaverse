package com.sharmachait.ws.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthResponse {
    private String jwt;
    private Boolean status=false;
    private String message;
    private String session = null;
    private String userId;
}