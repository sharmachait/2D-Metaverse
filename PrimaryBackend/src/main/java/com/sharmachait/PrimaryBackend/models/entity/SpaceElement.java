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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", referencedColumnName = "id")
    @JsonBackReference
    private Space space;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "element_id", referencedColumnName = "id")
    @JsonBackReference
    private Element element;

//    private boolean isStatic;
    private int x;
    private int y;
}
