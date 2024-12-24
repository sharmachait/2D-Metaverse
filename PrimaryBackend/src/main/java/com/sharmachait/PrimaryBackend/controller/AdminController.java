package com.sharmachait.PrimaryBackend.controller;

import com.sharmachait.PrimaryBackend.models.dto.AvatarDto;
import com.sharmachait.PrimaryBackend.models.dto.ElementDto;
import com.sharmachait.PrimaryBackend.models.dto.GameMapDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    @PostMapping("/element")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> postElement(@RequestBody ElementDto elementDto){

    }

    @PutMapping("/element/{elementId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> putElement(@RequestBody ElementDto elementDto,
                                         @PathVariable String elementId){

    }

    @PostMapping("/avatar")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> postAvatar(@RequestBody AvatarDto avatarDto){

    }

    @PostMapping("/map")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> postMap(@RequestBody GameMapDto gameMapDto){

    }
}
