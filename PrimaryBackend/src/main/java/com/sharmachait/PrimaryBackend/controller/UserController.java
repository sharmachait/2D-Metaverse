package com.sharmachait.PrimaryBackend.controller;

import com.sharmachait.PrimaryBackend.config.jwt.JwtProvider;
import com.sharmachait.PrimaryBackend.models.dto.UserDto;
import com.sharmachait.PrimaryBackend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/metadata")
    public ResponseEntity<UserDto> metadata(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try{
            String userId = JwtProvider.getIdFromToken(authorizationHeader);
            UserDto user = userService.findById(userId);
            return ResponseEntity.ok(user);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/metadata")
    public ResponseEntity<UserDto> postMetadata(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody UserDto userDto) throws Exception {
        try{
            String userId = JwtProvider.getIdFromToken(authorizationHeader);
            return ResponseEntity.ok(userService.update(userDto, userId));
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }
    @GetMapping("/metadata/bulk")
    public ResponseEntity<?> bulkMetadata(@RequestParam("ids") String userIdsParam){
        String cleanUserIds = userIdsParam.replaceAll("\\[|\\]", "").trim();
        String[] userIds = cleanUserIds.split(",");

        try{
            List<UserDto> users = new ArrayList<>();
            for(String id:userIds){
                UserDto user = userService.findById(id);
                UserDto userDto = UserDto.builder()
                        .role(user.getRole())
                        .avatarId(user.getAvatarId())
                        .build();
                users.add(userDto);
            }
            return ResponseEntity.ok(users);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }
}































