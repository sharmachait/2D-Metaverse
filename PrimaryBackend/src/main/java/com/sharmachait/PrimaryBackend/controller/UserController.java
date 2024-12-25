package com.sharmachait.PrimaryBackend.controller;

import com.sharmachait.PrimaryBackend.models.dto.UserDto;
import com.sharmachait.PrimaryBackend.models.entity.Avatar;
import com.sharmachait.PrimaryBackend.models.entity.User;
import com.sharmachait.PrimaryBackend.service.avatar.AvatarService;
import com.sharmachait.PrimaryBackend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AvatarService avatarService;
    @GetMapping("/metadata/{userId}")
    public ResponseEntity<UserDto> metadata(@PathVariable String userId) {
        User user = userService.findById(userId);
        UserDto userDto = UserDto.builder()
                .role(user.getRole())
                .avatarId(user.getAvatar() == null ? null : user.getAvatar().getId())
                .build();
        return ResponseEntity.ok(userDto);
    }
    @PostMapping("/metadata/{userId}")
    public ResponseEntity<UserDto> postMetadata(@PathVariable String userId, @RequestBody UserDto userDto) {
        Avatar avatar;
        try{
             avatar = avatarService.findById(userDto.getAvatarId());
        }catch (Exception e){
            avatar = null;
        }
        User user = userService.findById(userId);
        user.setAvatar(avatar);
//        user.setRole(userDto.getRole());
        userService.save(user);

        return ResponseEntity.ok(userDto);
    }
//    @GetMapping("/metadata/bulk/{spaceId}")
//    public ResponseEntity<?> bulkMetadata(@PathVariable String spaceId){
//
//    }
//    @PutMapping("/metadata/{userId}")
//    public ResponseEntity<?> putUser(@PathVariable String userId){
//
//    }
}
