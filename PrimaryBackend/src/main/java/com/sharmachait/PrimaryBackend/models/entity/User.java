package com.sharmachait.PrimaryBackend.models.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String username;

    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "avatar_id", referencedColumnName = "id")
    @JsonBackReference
    private Avatar avatar;

    private Role role = Role.ROLE_USER;

}
