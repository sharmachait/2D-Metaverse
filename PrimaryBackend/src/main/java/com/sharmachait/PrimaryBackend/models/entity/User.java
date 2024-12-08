package com.sharmachait.PrimaryBackend.models.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    public String getId(){
        return id;
    }

    private String username;
    public String getUsername() {
        return username;
    }
    private String password;
    public String getPassword() {
        return password;
    }
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "avatar_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private Avatar avatar;
    public Avatar getAvatar() {
        return avatar;
    }
    private Role role;
    public Role getRole() {
        return role;
    }
}
