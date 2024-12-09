package com.sharmachait.PrimaryBackend.models.dto;

import com.sharmachait.PrimaryBackend.models.entity.Role;
import lombok.Data;

@Data
public class LoginDto {
    private String username;
    private String password;
    private Role role;
}
