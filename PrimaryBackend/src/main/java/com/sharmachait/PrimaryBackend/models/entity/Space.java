package com.sharmachait.PrimaryBackend.models.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
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

  @OneToMany(mappedBy = "space", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  private Set<SpaceElement> spaceElements = new HashSet<>();

  @ManyToOne(fetch = FetchType.EAGER, optional = true)
  @JoinColumn(name = "map_id", referencedColumnName = "id")
  @JsonBackReference
  private GameMap gameMap;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "owner_id", referencedColumnName = "id")
  @JsonBackReference
  private User owner;

  @Override
  public int hashCode() {
    return Objects.hash(id); // only use the ID, not any collections
  }

  @Override
  public String toString() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Space that = (Space) o;
    return Objects.equals(id, that.id);
  }
}
