package com.sharmachait.PrimaryBackend.service.space;

import com.sharmachait.PrimaryBackend.models.dto.GameMapDto;
import com.sharmachait.PrimaryBackend.models.dto.SpaceDto;
import com.sharmachait.PrimaryBackend.models.dto.SpaceElementDto;
import com.sharmachait.PrimaryBackend.models.entity.GameMap;
import com.sharmachait.PrimaryBackend.models.entity.Space;
import com.sharmachait.PrimaryBackend.models.entity.User;

import java.util.List;
import java.util.NoSuchElementException;

public interface SpaceService {
  SpaceDto findById(String id) throws NoSuchElementException;

  // Space save(String authHeader, SpaceDto spaceDto);
  SpaceDto save(String owner, String gameMap, SpaceDto spaceDto) throws Exception;

  void deleteById(String authHeader, String spaceId) throws Exception;

  SpaceDto save(Space space);

  SpaceDto addElement(String authHeader, SpaceElementDto spaceElementDto, String spaceId) throws Exception;

  List<SpaceDto> findByUserId(String authHeader) throws Exception;

  SpaceDto deleteElement(String authHeader, String elementId) throws Exception;
}