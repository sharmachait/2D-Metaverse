package com.sharmachait.PrimaryBackend.repository;

import com.sharmachait.PrimaryBackend.models.entity.MapElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MapElementRepository extends JpaRepository<MapElement, String> {
  List<MapElement> findByGameMapId(String gameMapId);
}
