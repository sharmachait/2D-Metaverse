package com.sharmachait.PrimaryBackend.service.user;

import com.sharmachait.PrimaryBackend.models.entity.User;

import java.util.NoSuchElementException;

public interface UserService {
    User findByUsername(String username) throws NoSuchElementException;
    User findById(String id) throws NoSuchElementException;
    User save(User user);
}
