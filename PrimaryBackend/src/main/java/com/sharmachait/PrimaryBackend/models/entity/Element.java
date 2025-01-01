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
public class Element {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private int height;
    private int width;
    private String imageUrl;
    private boolean isStatic;
    @OneToMany(
            mappedBy = "element",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JsonManagedReference
    private Set<SpaceElement> spaceElements;

    @OneToMany(
            mappedBy = "element",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JsonManagedReference
    private Set<MapElement> mapElements;

}
