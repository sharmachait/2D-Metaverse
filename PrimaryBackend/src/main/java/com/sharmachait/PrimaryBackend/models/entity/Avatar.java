package com.sharmachait.PrimaryBackend.models.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String imageUrl;
    private String name;

    @OneToMany(
            mappedBy = "avatar",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST
    )
    @JsonManagedReference
    private Set<User> users;
}