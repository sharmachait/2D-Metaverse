package com.sharmachait.PrimaryBackend.service.user;

import com.sharmachait.PrimaryBackend.models.dto.SpaceDto;
import com.sharmachait.PrimaryBackend.models.dto.SpaceElementDto;
import com.sharmachait.PrimaryBackend.models.dto.UserDto;
import com.sharmachait.PrimaryBackend.models.entity.Avatar;
import com.sharmachait.PrimaryBackend.models.entity.Space;
import com.sharmachait.PrimaryBackend.models.entity.SpaceElement;
import com.sharmachait.PrimaryBackend.models.entity.User;
import com.sharmachait.PrimaryBackend.repository.AvatarRepository;
import com.sharmachait.PrimaryBackend.repository.UserRepository;
import com.sharmachait.PrimaryBackend.service.space.SpaceService;
import com.sharmachait.PrimaryBackend.service.space.SpaceServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service

public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    AvatarRepository avatarRepository;
    @Autowired
    SpaceServiceImpl spaceService;
    @Override
    public UserDto findByUsername(String username) throws NoSuchElementException {

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NoSuchElementException("No Such User");
        }
        return userToUserDto(user);
    }

    @Override
    public UserDto findById(String id) throws NoSuchElementException {
        try {
            System.out.println("Attempting to find user with ID: " + id);  // Add this
            User user = userRepository.findById(id).orElseThrow(()->new NoSuchElementException("No Such User"));
            return userToUserDto(user);
        }
        catch (Exception e){
            System.out.println("Error finding user with ID: " + id);  // Add this
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public UserDto save(User user) {
        return userToUserDto(userRepository.save(user));
    }
    @Transactional
    @Override
    public UserDto update(UserDto userDto,String userId) throws Exception {
        Avatar avatar = avatarRepository.findById(userDto.getAvatarId()).orElse(null);
        if(avatar == null) {
            throw new Exception("No Such Avatar");
        }
        User user = userRepository.findById(userId).orElseThrow(()->new Exception("No Such User"));

        user.setAvatar(avatar);
        userDto = new UserDto();
        if(user.getAvatar()!=null) {
            userDto.setAvatarId(user.getAvatar().getId());
        }
        userDto.setRole(user.getRole());
        userDto.setUsername(user.getUsername());
        userDto.setId(user.getId());
        Set<SpaceDto> spaces = new HashSet<>();
        if(user.getOwnedSpaces()!=null) {
            for(Space space: user.getOwnedSpaces()){
                spaces.add(spaceService.mapSpaceToSpaceDto(space));
            }
        }
        userDto.setSpaces(spaces);
        return userDto;
    }

    private UserDto userToUserDto(User user) {
        UserDto userDto = new UserDto();
        if(user.getAvatar()!=null) {
            userDto.setAvatarId(user.getAvatar().getId());
        }
        userDto.setRole(user.getRole());
        userDto.setUsername(user.getUsername());
        userDto.setId(user.getId());
        Set<SpaceDto> spaces = new HashSet<>();
        if(user.getOwnedSpaces()!=null) {
            for(Space space: user.getOwnedSpaces()){
                spaces.add(spaceService.mapSpaceToSpaceDto(space));
            }
        }
        userDto.setSpaces(spaces);
        return userDto;
    }
}
