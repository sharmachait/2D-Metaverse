package com.sharmachait.ws.models.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Avatar {
    private String id;
    private String imageUrl;
    private String name;
    private Set<User> users;

    @Override
    public int hashCode() {
        return Objects.hash(id); // only use the ID, not any collections
    }

    @Override
    public String toString() {
        return id;
    }
}