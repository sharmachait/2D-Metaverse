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
public class Avatar {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String imageUrl;
  private String name;

  @OneToMany(mappedBy = "avatar", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
  @JsonManagedReference
  private Set<User> users;

  @Override
  public int hashCode() {
    return Objects.hash(id); // only use the ID, not any collections
  }

  @Override
  public String toString() {
    return id;
  }
}