package com.sharmachait.PrimaryBackend.models.dto;

import com.sharmachait.PrimaryBackend.models.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private String avatarId;
    private Role role;
}
