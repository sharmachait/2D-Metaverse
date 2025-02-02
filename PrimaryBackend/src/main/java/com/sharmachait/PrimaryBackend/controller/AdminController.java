package com.sharmachait.PrimaryBackend.controller;

import com.sharmachait.PrimaryBackend.models.dto.ElementDto;
import com.sharmachait.PrimaryBackend.service.element.ElementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final ElementService elementService;
    @PostMapping("/element")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> postElement(@RequestBody ElementDto elementDto){
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(elementService.save(elementDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/element/{elementId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> putElement(@RequestBody ElementDto elementDto,
                                         @PathVariable String elementId){
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(elementService.update(elementId, elementDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
//
//    @PostMapping("/avatar")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public ResponseEntity<?> postAvatar(@RequestBody AvatarDto avatarDto){
//
//    }
//
//    @PostMapping("/map")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public ResponseEntity<?> postMap(@RequestBody GameMapDto gameMapDto){
//
//    }
}
