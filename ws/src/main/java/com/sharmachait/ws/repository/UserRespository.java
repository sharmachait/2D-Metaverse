package com.sharmachait.ws.repository;

import com.sharmachait.ws.models.entity.Status;
import com.sharmachait.ws.models.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRespository extends JpaRepository<User, String> {
    void deleteByUsernameAndSpaceId(String username, String spaceId);
    User findByUsernameAndSpaceId(String username, String spaceId);
    List<User> findBySpaceIdAndStatus(String spaceId, Status status);
}
