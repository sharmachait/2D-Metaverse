package com.sharmachait.ws.models.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
    private String id;
    private String username;
    private String password;
    private Status status;
    private Avatar avatar;
    private Role role;

    @Override
    public int hashCode() {
        return Objects.hash(id); // only use the ID, not any collections
    }

    @Override
    public String toString() {
        return id;
    }
}
