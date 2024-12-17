package com.sharmachait.ws.repository;

import com.sharmachait.ws.model.Status;
import com.sharmachait.ws.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByUsername(String username);
    public List<User> findAllByStatus(Status status);
}
