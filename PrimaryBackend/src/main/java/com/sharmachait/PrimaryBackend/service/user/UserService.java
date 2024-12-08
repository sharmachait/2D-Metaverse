package com.sharmachait.PrimaryBackend.service.user;

import com.sharmachait.PrimaryBackend.models.entity.User;

import java.util.NoSuchElementException;

public interface UserService {
    public User findByUsername(String username) throws NoSuchElementException;
    public User save(User user);
}
