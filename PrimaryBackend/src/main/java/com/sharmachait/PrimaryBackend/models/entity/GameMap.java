package com.sharmachait.PrimaryBackend.models.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
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
            cascade = CascadeType.ALL
    )
    @JsonManagedReference
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<MapElement> mapElements = new HashSet<>();

    private String thumbnail;
    @OneToMany(
            mappedBy = "gameMap",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JsonManagedReference
    private Set<Space> spaces= new HashSet<>();

    @Override
    public String toString() {
        return "GameMap{" +
                "id='" + id + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", name='" + name + '\'' +
                '}';
    }

}
