package com.sharmachait.PrimaryBackend.controller;

import com.sharmachait.PrimaryBackend.config.jwt.JwtProvider;
import com.sharmachait.PrimaryBackend.models.dto.SpaceDto;
import com.sharmachait.PrimaryBackend.models.dto.SpaceElementDto;
import com.sharmachait.PrimaryBackend.service.space.SpaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/space")
public class SpaceController {
  private final SpaceService spaceService;

  @PostMapping
  public ResponseEntity<?> postSpace(@RequestBody @Valid SpaceDto spaceDto,
      @RequestHeader("Authorization") String authHeader) {
    try {
      String userId = JwtProvider.getIdFromToken(authHeader);
      String gameMapId = spaceDto.getMapId();

      SpaceDto space = spaceService.save(userId, gameMapId, spaceDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(space);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @GetMapping("/all")
  public ResponseEntity<?> getSpaces(@RequestHeader("Authorization") String authHeader) {
    try {
      List<SpaceDto> spaces = spaceService.findByUserId(authHeader);
      return ResponseEntity.status(HttpStatus.OK).body(spaces);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @DeleteMapping("/{spaceId}")
  public ResponseEntity<?> deleteSpace(@PathVariable String spaceId,
      @RequestHeader("Authorization") String authHeader) {
    try {
      spaceService.deleteById(authHeader, spaceId);
      return ResponseEntity.status(HttpStatus.OK).body("Space Deleted");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  //
  @GetMapping("/{spaceId}")
  public ResponseEntity<?> getSpace(@PathVariable String spaceId) {
    try {
      SpaceDto space = spaceService.findById(spaceId);
      return ResponseEntity.status(HttpStatus.OK).body(space);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());
    }
  }

  //
  @PostMapping("/element/{spaceId}")
  public ResponseEntity<?> postSpaceElement(
      @RequestBody SpaceElementDto spaceElementDto,
      @PathVariable String spaceId,
      @RequestHeader("Authorization") String authHeader) {
    try {
      SpaceDto space = spaceService.addElement(authHeader, spaceElementDto, spaceId);
      return ResponseEntity.status(HttpStatus.CREATED).body(space);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());
    }
  }

  @DeleteMapping("/element/{elementId}")
  public ResponseEntity<?> deleteSpaceElement(@PathVariable String elementId,
      @RequestHeader("Authorization") String authHeader) {
    try {
      SpaceDto space = spaceService.deleteElement(authHeader, elementId);
      return ResponseEntity.status(HttpStatus.OK).body(space);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());
    }
  }
}
