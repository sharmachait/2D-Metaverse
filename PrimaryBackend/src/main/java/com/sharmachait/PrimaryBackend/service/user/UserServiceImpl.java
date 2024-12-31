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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service

public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    AvatarRepository avatarRepository;
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
        User user = userRepository.findById(id).orElseThrow(()->new NoSuchElementException("No Such User"));
        return userToUserDto(user);
    }

    @Override
    public UserDto save(User user) {
        return userToUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(UserDto userDto,String userId) throws Exception {
        Avatar avatar = avatarRepository.findById(userDto.getAvatarId()).orElse(null);
        if(avatar == null) {
            throw new Exception("No Such Avatar");
        }
        User user = userRepository.findById(userId).orElseThrow(()->new Exception("No Such User"));

        user.setAvatar(avatar);
        return save(user);
    }

    private UserDto userToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setAvatarId(user.getAvatar().getId());
        userDto.setRole(user.getRole());
        userDto.setUsername(user.getUsername());
        userDto.setId(user.getId());
        Set<SpaceDto> spaces = new HashSet<>();
        for(Space space: user.getOwnedSpaces()){
            spaces.add(mapSpaceToSpaceDto(space));
        }
        userDto.setSpaces(spaces);
        return userDto;
    }

    public SpaceDto mapSpaceToSpaceDto(Space spaceEntity) {
        SpaceDto spaceDto = SpaceDto.builder()
                .mapId(spaceEntity.getGameMap() == null ? null: spaceEntity.getGameMap().getId())
                .name(spaceEntity.getName())
                .dimensions(spaceEntity.getHeight()+"x"+spaceEntity.getWidth())
                .thumbnail(spaceEntity.getThumbnail())
                .ownerId(spaceEntity.getOwner().getId())
                .id(spaceEntity.getId())
                .build();
        List<SpaceElementDto> spaceElementDtos = new ArrayList<>();
        for(SpaceElement spaceElement : spaceEntity.getSpaceElements()){
            SpaceElementDto spaceElementDto = SpaceElementDto.builder()
                    .y(spaceElement.getY())
                    .x(spaceElement.getX())
                    .isStatic(spaceElement.getElement().isStatic())
                    .elementId(spaceElement.getId())
                    .build();
            spaceElementDtos.add(spaceElementDto);
        }
        spaceDto.setElements(spaceElementDtos);
        return spaceDto;
    }
}
