package com.sharmachait.ws.model.dto;

import com.sharmachait.ws.model.entity.Role;
import lombok.Data;

@Data
public class LoginDto {
    private String username;
    private String password;
    private Role role;
}