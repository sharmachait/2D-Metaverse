package com.sharmachait.PrimaryBackend.repository;

import com.sharmachait.PrimaryBackend.models.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
}
