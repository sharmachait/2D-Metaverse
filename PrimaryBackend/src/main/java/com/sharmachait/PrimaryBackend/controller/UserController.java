package com.sharmachait.PrimaryBackend.controller;

import com.sharmachait.PrimaryBackend.config.jwt.JwtProvider;
import com.sharmachait.PrimaryBackend.models.dto.UserDto;
import com.sharmachait.PrimaryBackend.models.entity.Avatar;
import com.sharmachait.PrimaryBackend.models.entity.User;
import com.sharmachait.PrimaryBackend.service.avatar.AvatarService;
import com.sharmachait.PrimaryBackend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AvatarService avatarService;

    @GetMapping("/metadata")
    public ResponseEntity<UserDto> metadata(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try{
            String userId = JwtProvider.getIdFromToken(authorizationHeader);
            User user = userService.findById(userId);
            UserDto userDto = UserDto.builder()
                    .role(user.getRole())
                    .avatarId(user.getAvatar() == null ? null : user.getAvatar().getId())
                    .build();
            return ResponseEntity.ok(userDto);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/metadata")
    public ResponseEntity<UserDto> postMetadata(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody UserDto userDto) {
        Avatar avatar;
        String userId = JwtProvider.getIdFromToken(authorizationHeader);
        try{
            avatar = avatarService.findById(userDto.getAvatarId());
        }catch (Exception e){
            avatar = null;
        }
        User user = userService.findById(userId);
        user.setAvatar(avatar);
//      user.setRole(userDto.getRole());
        userService.save(user);
        return ResponseEntity.ok(userDto);

    }
    @GetMapping("/metadata/bulk")
    public ResponseEntity<?> bulkMetadata(@RequestParam("ids") String userIdsParam){
        String cleanUserIds = userIdsParam.replaceAll("\\[|\\]", "").trim();
        List<String> userIds = Arrays.asList(cleanUserIds.split(","));
        try{
            List<UserDto> users = new ArrayList<>();
            for(String id:userIds){
                User user = userService.findById(id);
                UserDto userDto = UserDto.builder()
                        .role(user.getRole())
                        .avatarId(user.getAvatar() == null ? null : user.getAvatar().getId())
                        .build();
                users.add(userDto);
            }
            return ResponseEntity.ok(users);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
