package com.sharmachait.ws.models.dto;

import com.sharmachait.ws.models.entity.Role;
import lombok.Data;

@Data
public class LoginDto {
    private String username;
    private String password;
    private Role role;
}