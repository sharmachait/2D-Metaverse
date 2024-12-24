package com.sharmachait.PrimaryBackend.controller;

import com.sharmachait.PrimaryBackend.models.dto.SpaceDto;
import com.sharmachait.PrimaryBackend.models.dto.SpaceElementDto;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Data
@RequestMapping("/api/v1/space")
public class SpaceController {
    @PostMapping
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> postSpace(@RequestBody SpaceDto spaceDto) {

    }
    @GetMapping("/all")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getSpaces() {

    }

    @DeleteMapping("/{spaceId}")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteSpace(@PathVariable String spaceId) {

    }

    @GetMapping("/{spaceId}")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getSpace(@PathVariable String spaceId) {

    }

    @PostMapping("/element/{spaceId}")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> postSpaceElement(@RequestBody SpaceElementDto spaceElementDto, @PathVariable String spaceId) {

    }

    @DeleteMapping("/element/{spaceId}")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> postSpaceElement(@RequestBody SpaceElementDto spaceElementDto, @PathVariable String spaceId) {

    }
}
