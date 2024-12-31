package com.sharmachait.PrimaryBackend.service.gameMap;

import com.sharmachait.PrimaryBackend.models.dto.GameMapDto;
import com.sharmachait.PrimaryBackend.models.dto.MapElementDto;
import com.sharmachait.PrimaryBackend.models.entity.Element;
import com.sharmachait.PrimaryBackend.models.entity.GameMap;
import com.sharmachait.PrimaryBackend.models.entity.MapElement;
import com.sharmachait.PrimaryBackend.repository.GameMapRepository;
import com.sharmachait.PrimaryBackend.repository.MapElementRepository;
import com.sharmachait.PrimaryBackend.service.element.ElementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameMapServiceImpl implements GameMapService {
    private final GameMapRepository gameMapRepository;
    private final ElementService elementService;
    private final MapElementRepository mapElementRepository;
    @Override
    public GameMap findById(String id) {
        System.out.println("Received ID: " + id);
        System.out.println("ID class type: " + (id != null ? id.getClass().getName() : "null"));
        try {
            Optional<GameMap> mapOptional = gameMapRepository.findById(id);
            System.out.println("Found map: " + mapOptional.isPresent());
            GameMap map = mapOptional.orElse(null);
            return map;
        } catch(Exception e) {
            System.out.println("Exception type: " + e.getClass().getName());
            System.out.println("Exception message: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public GameMap save(GameMap gameMap) {
        return gameMapRepository.save(gameMap);
    }

    @Override
    @Transactional
    public GameMap save(GameMapDto gameMapDto) throws Exception {
        return mapDtoToGameMap(gameMapDto);
    }
    @Transactional
    protected GameMap mapDtoToGameMap(GameMapDto gameMapDto) throws Exception {
        if (!gameMapDto.getDimensions().matches("^[0-9]{1,3}x[0-9]{1,3}$")) {
            throw new IllegalArgumentException("Invalid dimensions format: " + gameMapDto.getDimensions());
        }

        GameMap gameMap = new GameMap();
        int iX = gameMapDto.getDimensions().indexOf('x');
        String height = gameMapDto.getDimensions().substring(0, iX);
        String width = gameMapDto.getDimensions().substring(iX + 1);
//        gameMap.setMapElements(elements);
        gameMap.setName(gameMapDto.getName());
        gameMap.setHeight(Integer.parseInt(height));
        gameMap.setWidth(Integer.parseInt(width));
        gameMap.setThumbnail(gameMapDto.getThumbnail());
        gameMap = gameMapRepository.save(gameMap);
        if(gameMapDto.getMapElements()!=null) {
            for(MapElementDto mapElementDto : gameMapDto.getMapElements()) {
                mapElementDtoToMapElement(gameMap, mapElementDto);
            }
        }
        return gameMap;

    }
    @Transactional
    protected void mapElementDtoToMapElement(GameMap gameMap, MapElementDto mapElementDto) throws Exception {
        Element element = elementService.getElementById(mapElementDto.getElementId());
        MapElement mapElement =  MapElement.builder()
                .element(element)
                .gameMap(gameMap)
                .y(mapElementDto.getY())
                .x(mapElementDto.getX())
                .build();
        mapElementRepository.save(mapElement);
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
