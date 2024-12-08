package com.sharmachait.PrimaryBackend.models.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class MapElement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "map_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private GameMap gameMap;
    private String elementId;
    private int x;
    private int y;
}
