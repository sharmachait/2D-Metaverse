package com.sharmachait.PrimaryBackend.models.dto;

import com.sharmachait.PrimaryBackend.models.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    private String username;
    private String password;
    private Role role;
}
