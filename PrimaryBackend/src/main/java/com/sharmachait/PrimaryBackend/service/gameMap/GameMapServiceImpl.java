package com.sharmachait.PrimaryBackend.service.gameMap;

import com.sharmachait.PrimaryBackend.models.dto.GameMapDto;
import com.sharmachait.PrimaryBackend.models.dto.MapElementDto;
import com.sharmachait.PrimaryBackend.models.entity.Element;
import com.sharmachait.PrimaryBackend.models.entity.GameMap;
import com.sharmachait.PrimaryBackend.models.entity.MapElement;
import com.sharmachait.PrimaryBackend.repository.GameMapRepository;
import com.sharmachait.PrimaryBackend.service.element.ElementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GameMapServiceImpl implements GameMapService {
    private final GameMapRepository gameMapRepository;
    private final ElementService elementService;
    @Override
    public GameMap findById(String id) {
        return gameMapRepository.findById(id).orElse(null);
    }

    @Override
    public GameMap save(GameMap gameMap) {
        return gameMapRepository.save(gameMap);
    }

    @Override
    public GameMap save(GameMapDto gameMap) throws Exception {
        return save(mapDtoToGameMap(gameMap));
    }

    private GameMap mapDtoToGameMap(GameMapDto gameMapDto) throws Exception {
        if (!gameMapDto.getDimensions().matches("^[0-9]{1,3}x[0-9]{1,3}$")) {
            throw new IllegalArgumentException("Invalid dimensions format: " + gameMapDto.getDimensions());
        }
        Set<MapElement> elements = new HashSet<>();
        GameMap gameMap = new GameMap();
        if(gameMapDto.getMapElements()!=null) {
            for(MapElementDto mapElementDto : gameMapDto.getMapElements()) {
                elements.add(mapElementDtoToMapElement(gameMap, mapElementDto));
            }
        }

        int iX = gameMapDto.getDimensions().indexOf('x');
        String height = gameMapDto.getDimensions().substring(0, iX);
        String width = gameMapDto.getDimensions().substring(iX + 1);
        gameMap.setMapElements(elements);
        gameMap.setName(gameMapDto.getName());
        gameMap.setHeight(Integer.parseInt(height));
        gameMap.setWidth(Integer.parseInt(width));
        gameMap.setThumbnail(gameMapDto.getThumbnail());
        return gameMap;

    }

    private MapElement mapElementDtoToMapElement(GameMap gameMap, MapElementDto mapElementDto) throws Exception {
        Element element = elementService.getElementById(mapElementDto.getElementId());
        return MapElement.builder()
                .element(element)
                .gameMap(gameMap)
                .y(mapElementDto.getY())
                .x(mapElementDto.getX())
                .build();
    }

    private GameMapDto mapGameMapToDto(GameMap gameMap) throws Exception {
        List<MapElementDto> elements = new ArrayList<>();
        GameMapDto gameMapDto = new GameMapDto();
        if(gameMap.getMapElements()!=null) {
            for(MapElement mapElement : gameMap.getMapElements()) {
                elements.add(mapElementToMapElementDto(mapElement));
            }
        }


        gameMapDto.setMapElements(elements);
        gameMapDto.setName(gameMap.getName());
        gameMapDto.setDimensions(gameMap.getHeight() + "x" + gameMap.getWidth());
        gameMapDto.setThumbnail(gameMap.getThumbnail());
        gameMapDto.setId(gameMap.getId());
        return gameMapDto;
    }

    private MapElementDto mapElementToMapElementDto(MapElement mapElement) throws Exception {

        return MapElementDto.builder()
                .id(mapElement.getId())
                .elementId(mapElement.getElement().getId())
                .gameMapId(mapElement.getGameMap().getId())
                .y(mapElement.getY())
                .x(mapElement.getX())
                .build();
    }
}
