package com.sharmachait.ws.service;

import com.sharmachait.ws.models.dto.UserDto;
import com.sharmachait.ws.models.entity.Role;
import com.sharmachait.ws.models.entity.Status;
import com.sharmachait.ws.models.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    @Qualifier("metaverseJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public List<UserDto> findAllBySpaceIdAndStatus(String spaceId, String status) {

        List<UserDto> users = jdbcTemplate.query(
                "SELECT DISTINCT u.* FROM users u " +
                        "INNER JOIN spaces s ON s.owner_id = u.id " +
                        "WHERE s.id = ?"
                , new Object[]{spaceId},
                (rs, rowNum) -> {
                    UserDto user = new UserDto();
                    user.setId(rs.getString("id"));
                    user.setAvatarId(rs.getString("avatar_id"));
                    user.setUsername(rs.getString("username"));

                    String roleString = rs.getString("role");
                    if (roleString != null) {
                        user.setRole(Role.valueOf(roleString));  // Safer way to convert string to enum
                    }

                    return user;
                }
        );

        // get all from jpa user and find users that have status online in this table

        return users;
    }
}
