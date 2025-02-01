package com.sharmachait.PrimaryBackend.controller;

import com.sharmachait.PrimaryBackend.models.dto.AvatarDto;
import com.sharmachait.PrimaryBackend.models.dto.ElementDto;
import com.sharmachait.PrimaryBackend.models.dto.GameMapDto;
import com.sharmachait.PrimaryBackend.service.avatar.AvatarService;
import com.sharmachait.PrimaryBackend.service.element.ElementService;
import com.sharmachait.PrimaryBackend.service.gameMap.GameMapService;
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
  private final AvatarService avatarService;
  private final GameMapService gameMapService;

  @PostMapping("/element")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<?> postElement(@RequestBody ElementDto elementDto) {
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
      @PathVariable String elementId) {
    try {
      return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(elementService.update(elementId, elementDto));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  //
  @PostMapping("/avatar")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<?> postAvatar(@RequestBody AvatarDto avatarDto) {
    try {
      return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(avatarService.save(avatarDto));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  //
  @PostMapping("/map")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<?> postMap(@RequestBody GameMapDto gameMapDto) {
    try {
      gameMapDto = gameMapService.save(gameMapDto);
      return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(gameMapDto);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @GetMapping("/map/{mapid}")
  public ResponseEntity<?> postMap(@PathVariable String mapid) {
    try {
      return ResponseEntity
          .status(HttpStatus.OK)
          .body(gameMapService.findById(mapid));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }
}
