package com.sharmachait.PrimaryBackend.models.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SpaceElement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "space_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private Space space;
    private String elementId;
    private int x;
    private int y;
}
