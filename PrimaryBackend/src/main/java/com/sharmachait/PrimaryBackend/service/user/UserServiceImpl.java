package com.sharmachait.PrimaryBackend.service.user;

import com.sharmachait.PrimaryBackend.models.entity.User;
import com.sharmachait.PrimaryBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Override
    public User findByUsername(String username) throws NoSuchElementException {
        if(username == null|| username.isBlank()||username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NoSuchElementException("No Such User");
        }
        return user;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}
