package com.sharmachait.PrimaryBackend.repository;

import com.sharmachait.PrimaryBackend.models.entity.Element;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElementRepository extends JpaRepository<Element, String> {
}
