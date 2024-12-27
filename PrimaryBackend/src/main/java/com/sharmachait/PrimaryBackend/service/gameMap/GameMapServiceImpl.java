package com.sharmachait.PrimaryBackend.service.gameMap;

import com.sharmachait.PrimaryBackend.models.entity.GameMap;
import com.sharmachait.PrimaryBackend.repository.GameMapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameMapServiceImpl implements GameMapService {
    private final GameMapRepository gameMapRepository;
    @Override
    public GameMap findById(String id) {
        return gameMapRepository.findById(id).orElse(null);
    }

    @Override
    public GameMap save(GameMap gameMap) {
        return gameMapRepository.save(gameMap);
    }
}
