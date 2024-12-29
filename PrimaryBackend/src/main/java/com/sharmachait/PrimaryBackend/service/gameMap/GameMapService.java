package com.sharmachait.PrimaryBackend.service.gameMap;

import com.sharmachait.PrimaryBackend.models.dto.GameMapDto;
import com.sharmachait.PrimaryBackend.models.entity.GameMap;

public interface GameMapService {
    GameMap findById(String id);
    GameMap save(GameMap gameMap);

    GameMap save(GameMapDto gameMap) throws Exception;
}
