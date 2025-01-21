package com.sharmachait.PrimaryBackend.controller;

import com.sharmachait.PrimaryBackend.service.element.ElementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/element")
public class ElementController {
  private final ElementService elementService;

  @GetMapping
  // @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<?> getElements() {
    try {
      return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(elementService.getAllElements());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }
}
