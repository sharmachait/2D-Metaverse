package com.sharmachait.PrimaryBackend.service.space;

import com.sharmachait.PrimaryBackend.models.dto.SpaceDto;
import com.sharmachait.PrimaryBackend.models.dto.SpaceElementDto;
import com.sharmachait.PrimaryBackend.models.entity.Space;

import java.util.List;
import java.util.NoSuchElementException;

public interface SpaceService {
    SpaceDto findById(String id) throws NoSuchElementException;
//    Space save(String authHeader, SpaceDto spaceDto);
    SpaceDto save(String authHeader, SpaceDto spaceDto);
    void deleteById(String authHeader, String spaceId) throws Exception;
    Space save(Space spaceDto);
    SpaceDto addElement(String authHeader, SpaceElementDto spaceElementDto, String spaceId) throws Exception;
    List<SpaceDto> findByUserId(String authHeader);
    SpaceDto deleteElement(String authHeader,  String elementId) throws Exception;
}
