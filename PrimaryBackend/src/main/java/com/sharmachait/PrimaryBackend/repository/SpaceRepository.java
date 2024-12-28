package com.sharmachait.PrimaryBackend.repository;

import com.sharmachait.PrimaryBackend.models.entity.Space;
import com.sharmachait.PrimaryBackend.models.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SpaceRepository extends JpaRepository<Space, String> {
    List<Space> findByOwner(User owner);
}
