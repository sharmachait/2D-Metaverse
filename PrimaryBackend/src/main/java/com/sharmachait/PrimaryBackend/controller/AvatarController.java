package com.sharmachait.PrimaryBackend.controller;

import com.sharmachait.PrimaryBackend.service.avatar.AvatarService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Data
@RequestMapping("/api/v1/avatar")
public class AvatarController {
    private final AvatarService avatarService;
    @GetMapping
    public ResponseEntity<?> getAvatars(){
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(avatarService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
