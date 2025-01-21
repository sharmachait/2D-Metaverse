package com.sharmachait.PrimaryBackend.models.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
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
  @OneToMany(mappedBy = "element", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JsonManagedReference
  private Set<SpaceElement> spaceElements;

  @OneToMany(mappedBy = "element", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JsonManagedReference
  private Set<MapElement> mapElements;

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
    Element that = (Element) o;
    return Objects.equals(id, that.id);
  }
}
