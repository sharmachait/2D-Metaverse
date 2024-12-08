package com.sharmachait.PrimaryBackend.models.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

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
}
