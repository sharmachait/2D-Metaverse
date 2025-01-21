package com.sharmachait.PrimaryBackend.service.user;

import com.sharmachait.PrimaryBackend.models.entity.User;
import com.sharmachait.PrimaryBackend.models.dto.UserDto;

import java.util.NoSuchElementException;

public interface UserService {
  UserDto findByUsername(String username) throws NoSuchElementException;

  UserDto findById(String id) throws NoSuchElementException;

  UserDto save(User user);

  UserDto update(UserDto user, String userId) throws Exception;
}
