package com.sharmachait.ws.service;

import com.sharmachait.ws.models.dto.UserDto;
import com.sharmachait.ws.models.entity.Role;
import com.sharmachait.ws.models.entity.Status;
import com.sharmachait.ws.models.entity.User;
import com.sharmachait.ws.repository.UserRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

  @Autowired
  @Qualifier("metaverseJdbcTemplate")
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private UserRespository userRespository;

  public List<UserDto> findAllBySpaceIdAndStatus(String spaceId, String status) {

    // List<UserDto> users = jdbcTemplate.query(
    // "SELECT DISTINCT u.* FROM users u " +
    // "INNER JOIN spaces s ON s.owner_id = u.id " +
    // "WHERE s.id = ?"
    // , new Object[]{spaceId},
    // (rs, rowNum) -> {
    // UserDto user = new UserDto();
    // user.setId(rs.getString("id"));
    //
    // user.setUsername(rs.getString("username"));
    //
    // String roleString = rs.getString("role");
    // if (roleString != null) {
    // user.setRole(Role.valueOf(roleString)); // Safer way to convert string to
    // enum
    // }
    //
    // return user;
    // }
    // );

    // add to database when connect, remove from database when disconnect
    List<User> users = userRespository.findBySpaceIdAndStatus(spaceId, Status.valueOf(status));
    List<UserDto> userDtos = new ArrayList<>();

    for (User user : users) {
      UserDto userDto = new UserDto();
      userDto.setId(user.getId());
      userDto.setStatus(user.getStatus());
      userDto.setUsername(user.getUsername());
      userDto.setSpaceId(user.getSpaceId());
      userDto.setRole(user.getRole());
      userDtos.add(userDto);
    }
    return userDtos;
  }

  public User Disconnect(String email, String spaceId) {
    User user = userRespository.findByUsernameAndSpaceId(email, spaceId);
    user.setStatus(Status.OFFLINE);
    return userRespository.save(user);
  }
}
