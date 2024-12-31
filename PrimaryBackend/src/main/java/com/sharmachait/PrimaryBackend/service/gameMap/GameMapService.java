package com.sharmachait.PrimaryBackend.service.gameMap;

import com.sharmachait.PrimaryBackend.models.dto.GameMapDto;

public interface GameMapService {
    GameMapDto findById(String id) throws Exception;
    GameMapDto save(GameMapDto gameMap) throws Exception;
}
