package com.sharmachait.PrimaryBackend.repository;

import com.sharmachait.PrimaryBackend.models.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, String> {

}
