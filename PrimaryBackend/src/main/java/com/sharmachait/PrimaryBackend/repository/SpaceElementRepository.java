package com.sharmachait.PrimaryBackend.repository;

import com.sharmachait.PrimaryBackend.models.entity.SpaceElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpaceElementRepository extends JpaRepository<SpaceElement, String> {
}
