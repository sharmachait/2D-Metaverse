package com.sharmachait.PrimaryBackend.models.dto;

import com.sharmachait.PrimaryBackend.models.entity.Role;
import lombok.Data;

@Data
public class UserDto {
    private String avatarId;
    private Role role = Role.ROLE_USER;
}
