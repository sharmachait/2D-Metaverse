package com.sharmachait.PrimaryBackend.models.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private int width;
    private int height;
    private String thumbnail;
    @OneToMany(
            mappedBy = "space",
            fetch = FetchType.EAGER,
            cascade = CascadeType.PERSIST
    )
    @JsonManagedReference
    private Set<SpaceElement> spaceElements;
}
