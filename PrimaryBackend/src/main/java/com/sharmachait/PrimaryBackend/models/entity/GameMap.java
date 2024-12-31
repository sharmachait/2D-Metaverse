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
public class GameMap {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private int width;
    private int height;
    private String name;
    @OneToMany(
            mappedBy = "gameMap",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    @JsonManagedReference
    private Set<MapElement> mapElements;

    private String thumbnail;
    @OneToMany(
            mappedBy = "gameMap",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    @JsonManagedReference
    private Set<Space> spaces;
}
