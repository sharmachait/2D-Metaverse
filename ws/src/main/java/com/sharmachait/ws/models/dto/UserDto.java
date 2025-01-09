package com.sharmachait.ws.models.dto;

import com.sharmachait.ws.models.entity.Avatar;
import com.sharmachait.ws.models.entity.Role;
import com.sharmachait.ws.models.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private String id;
    private String avatarId;
    private Role role;
    private String username;
    private Status status;

    @Override
    public int hashCode() {
        return Objects.hash(id); // only use the ID, not any collections
    }
    @Override
    public String toString() {
        return id;
    }

}
