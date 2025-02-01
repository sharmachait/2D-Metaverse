package com.sharmachait.PrimaryBackend.models.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

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
  @JoinColumn(name = "space_id", referencedColumnName = "id")
  @JsonBackReference
  private Space space;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "element_id", referencedColumnName = "id")
  @JsonBackReference
  private Element element;

  // private boolean isStatic;
  private int x;
  private int y;

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
    SpaceElement that = (SpaceElement) o;
    return Objects.equals(id, that.id);
  }
}
