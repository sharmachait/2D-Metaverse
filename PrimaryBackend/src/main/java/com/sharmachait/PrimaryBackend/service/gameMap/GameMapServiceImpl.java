package com.sharmachait.PrimaryBackend.service.gameMap;

import com.sharmachait.PrimaryBackend.models.dto.ElementDto;
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

import java.util.*;

@Service
@RequiredArgsConstructor
public class GameMapServiceImpl implements GameMapService {
  private final GameMapRepository gameMapRepository;
  private final ElementService elementService;
  private final MapElementRepository mapElementRepository;

  @Override
  public GameMapDto findById(String id) throws Exception {
    GameMap gameMap = gameMapRepository.findById(id).orElseThrow(() -> new Exception("Game map not found"));
    return gameMapToGameMapDto(gameMap);
  }

  @Override
  @Transactional
  public GameMapDto save(GameMapDto gameMapDto) throws Exception {

    GameMap gameMap = new GameMap();
    gameMap.setId(gameMapDto.getId());
    gameMap.setName(gameMapDto.getName());
    gameMap.setThumbnail(gameMapDto.getThumbnail());
    int iX = gameMapDto.getDimensions().indexOf("x");
    int height = Integer.parseInt(gameMapDto.getDimensions().substring(0, iX));
    int width = Integer.parseInt(gameMapDto.getDimensions().substring(iX + 1));
    gameMap.setWidth(width);
    gameMap.setHeight(height);
    gameMap = gameMapRepository.save(gameMap);

    Set<MapElement> mapElements = new HashSet<>();
    if (gameMapDto.getMapElements() != null) {
      for (MapElementDto mapElementDto : gameMapDto.getMapElements()) {
        MapElement mapElement = mapElementDtoToMapElement(mapElementDto);
        mapElement.setGameMap(gameMap);
        mapElement = mapElementRepository.save(mapElement);

        mapElements.add(mapElement);
      }
    }
    gameMapDto = gameMapToGameMapDto(gameMap, mapElements);
    return gameMapDto;
  }

  private GameMapDto gameMapToGameMapDto(GameMap gameMap, Set<MapElement> mapElements) {
    GameMapDto gameMapDto = new GameMapDto();
    gameMapDto.setId(gameMap.getId());
    gameMapDto.setName(gameMap.getName());
    gameMapDto.setThumbnail(gameMap.getThumbnail());
    gameMapDto.setDimensions(gameMap.getHeight() + "x" + gameMap.getWidth());
    List<MapElementDto> elements = new ArrayList<>();

    for (MapElement mapElement : mapElements) {

      MapElementDto mapElementDto = new MapElementDto();
      mapElementDto.setId(mapElement.getId());
      mapElementDto.setGameMapId(gameMap.getId());
      mapElementDto.setY(mapElement.getY());
      mapElementDto.setX(mapElement.getX());
      if (mapElement.getElement() != null)
        mapElementDto.setElementId(mapElement.getElement().getId());

      elements.add(mapElementDto);
    }

    gameMapDto.setMapElements(elements);
    return gameMapDto;
  }

  private GameMapDto gameMapToGameMapDto(GameMap gameMap) {
    GameMapDto gameMapDto = new GameMapDto();
    gameMapDto.setId(gameMap.getId());
    gameMapDto.setName(gameMap.getName());
    gameMapDto.setThumbnail(gameMap.getThumbnail());
    gameMapDto.setDimensions(gameMap.getHeight() + "x" + gameMap.getWidth());
    List<MapElementDto> elements = new ArrayList<>();

    for (MapElement mapElement : gameMap.getMapElements()) {

      MapElementDto mapElementDto = new MapElementDto();
      mapElementDto.setId(mapElement.getId());
      mapElementDto.setGameMapId(gameMap.getId());
      mapElementDto.setY(mapElement.getY());
      mapElementDto.setX(mapElement.getX());
      if (mapElement.getElement() != null)
        mapElementDto.setElementId(mapElement.getElement().getId());

      elements.add(mapElementDto);
    }

    gameMapDto.setMapElements(elements);
    return gameMapDto;
  }

  private MapElement mapElementDtoToMapElement(MapElementDto mapElementDto) throws Exception {
    MapElement mapElement = new MapElement();
    mapElement.setId(mapElementDto.getId());
    mapElement.setY(mapElementDto.getY());
    mapElement.setX(mapElementDto.getX());
    if (mapElementDto.getGameMapId() != null) {
      GameMap gameMap = gameMapRepository.findById(mapElementDto.getGameMapId())
          .orElseThrow(() -> new Exception("Game map not found"));
      mapElement.setGameMap(gameMap);
    }
    if (mapElementDto.getElementId() != null) {
      ElementDto element = elementService.getElementById(mapElementDto.getElementId());
      mapElement.setElement(mapDtoToElement(element));
    }
    return mapElement;
  }

  public Element mapDtoToElement(ElementDto elementDto) {
    return Element.builder()
        .id(elementDto.getId())
        .isStatic(elementDto.getIsStatic())
        .width(elementDto.getWidth())
        .height(elementDto.getHeight())
        .imageUrl(elementDto.getImageUrl())
        .build();
  }

  private MapElementDto mapElementToMapElementDto(MapElement mapElement) {
    MapElementDto mapElementDto = new MapElementDto();
    mapElementDto.setId(mapElement.getId());
    if (mapElement.getGameMap() != null)
      mapElementDto.setGameMapId(mapElement.getGameMap().getId());
    mapElementDto.setY(mapElement.getY());
    mapElementDto.setX(mapElement.getX());
    if (mapElement.getElement() != null)
      mapElementDto.setElementId(mapElement.getElement().getId());
    return mapElementDto;
  }
}
