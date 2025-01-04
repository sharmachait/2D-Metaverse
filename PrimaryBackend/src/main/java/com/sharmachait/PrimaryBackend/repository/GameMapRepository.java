package com.sharmachait.PrimaryBackend.repository;

import com.sharmachait.PrimaryBackend.models.entity.GameMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameMapRepository extends JpaRepository<GameMap, String> {
    @Query("SELECT gm FROM GameMap gm LEFT JOIN FETCH gm.mapElements WHERE gm.id = :gameMapId")
    Optional<GameMap> findByIdWithMapElements(@Param("gameMapId") String gameMapId);

}
