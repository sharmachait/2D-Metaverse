package com.sharmachait.PrimaryBackend.service.space;

import com.sharmachait.PrimaryBackend.config.jwt.JwtProvider;
import com.sharmachait.PrimaryBackend.models.dto.SpaceDto;
import com.sharmachait.PrimaryBackend.models.dto.SpaceElementDto;
import com.sharmachait.PrimaryBackend.models.entity.*;
import com.sharmachait.PrimaryBackend.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SpaceServiceImpl implements SpaceService {

  private final SpaceRepository spaceRepository;
  private final UserRepository userRepository;
  private final ElementRepository elementRepository;
  private final SpaceElementRepository spaceElementRepository;
  private final GameMapRepository gameMapRepository;
  private final JdbcTemplate jdbc;
  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  @Override
  public void deleteById(String authHeader, String spaceId) throws Exception {
    String userId = JwtProvider.getIdFromToken(authHeader);
    System.out.println(spaceId);
    Space spaceEntity = spaceRepository.findById(spaceId).orElseThrow(() -> new Exception("Space not found"));

    if (spaceEntity.getOwner().getId().equals(userId)) {
      spaceRepository.deleteById(spaceId);
    } else {
      throw new Exception("Unauthorized");
    }
  }

  @Override
  public SpaceDto findById(String id) throws NoSuchElementException {
    Space space = spaceRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Space not found"));
    return mapSpaceToSpaceDto(space);
  }

  @Transactional
  @Override
  public SpaceDto save(String ownerID, String gameMapId, SpaceDto spaceDto) throws Exception {
    User owner = userRepository.findById(ownerID)
        .orElseThrow(() -> new Exception("User not found"));

    if (gameMapId == null && spaceDto.getDimensions() == null) {
      throw new Exception("Game map id not found and dimensions not specified");
    }
    int xIndex = spaceDto.getDimensions().indexOf('x');
    if (xIndex == -1) {
      throw new Exception("Invalid dimensions");
    }
    String height = spaceDto.getDimensions().substring(0, xIndex);
    String width = spaceDto.getDimensions().substring(xIndex + 1);

    Space spaceEntity = Space.builder()
        .name(spaceDto.getName())
        .height(Integer.parseInt(height))
        .width(Integer.parseInt(width))
        .owner(owner)
        .spaceElements(new HashSet<>())
        .build();

    GameMap gameMap;

    if (gameMapId != null) {

      gameMap = gameMapRepository.findById(gameMapId)
          .orElseThrow(() -> new Exception("GameMap not found"));

      spaceEntity.setHeight(gameMap.getHeight());
      spaceEntity.setWidth(gameMap.getWidth());

      // List<MapElement> mapElements = jdbc.query(
      // "select * from map_element where map_id = ?",
      // new Object[]{gameMapId},
      // new BeanPropertyRowMapper<>(MapElement.class)
      // );
      List<MapElement> mapElements = jdbc.query(
          "SELECT me.*, e.id AS element_id, e.height, e.width, e.image_url, e.is_static " +
              "FROM map_element me " +
              "JOIN element e ON me.element_id = e.id " +
              "WHERE me.map_id = ?",
          new Object[] { gameMapId },
          (rs, rowNum) -> {
            MapElement mapElement = new MapElement();
            mapElement.setId(rs.getString("id"));
            mapElement.setX(rs.getInt("x"));
            mapElement.setY(rs.getInt("y"));

            Element element = new Element();
            element.setId(rs.getString("element_id"));
            element.setHeight(rs.getInt("height"));
            element.setWidth(rs.getInt("width"));
            element.setImageUrl(rs.getString("image_url"));
            element.setStatic(rs.getBoolean("is_static"));

            mapElement.setElement(element);
            return mapElement;
          });

      Set<SpaceElement> spaceElements = new HashSet<>();
      for (MapElement mapElement : mapElements) {
        SpaceElement spaceElement = mapMapElementDtoToSpaceElement(mapElement, spaceEntity);
        spaceElementRepository.save(spaceElement);
        spaceElements.add(spaceElement);
      }

      spaceEntity.setThumbnail(spaceDto.getThumbnail() != null ? spaceDto.getThumbnail() : gameMap.getThumbnail());

      spaceEntity.setGameMap(gameMap);
    }

    Space savedSpace = spaceRepository.save(spaceEntity);
    savedSpace = spaceRepository.findById(savedSpace.getId()).orElseThrow(() -> new Exception("Space not found"));
    return mapSpaceToSpaceDto(savedSpace);
  }

  @Override
  public SpaceDto save(Space space) {
    return mapSpaceToSpaceDto(spaceRepository.save(space));
  }

  @Override
  @Transactional
  public SpaceDto addElement(String authHeader, SpaceElementDto spaceElementDto, String spaceId) throws Exception {
    String userId = JwtProvider.getIdFromToken(authHeader);

    Space spaceEntity = spaceRepository.findById(spaceId).orElse(null);
    if (spaceElementDto.getX() >= spaceEntity.getWidth() || spaceElementDto.getY() >= spaceEntity.getHeight()) {
      throw new Exception("out of bounds");
    }
    if (spaceEntity == null) {
      throw new Exception("Space not found");
    }
    if (!Objects.equals(userId, spaceEntity.getOwner().getId())) {
      throw new Exception("Unauthorized");
    }
    SpaceElement spaceElement = mapSpaceElementDtoToSpaceElement(spaceElementDto, spaceEntity);
    spaceElement = spaceElementRepository.save(spaceElement);
    spaceEntity.getSpaceElements().add(spaceElement);
    Space space = spaceRepository.save(spaceEntity);
    spaceRepository.flush();
    entityManager.clear();
    return mapSpaceToSpaceDto(space);
  }

  @Override
  public List<SpaceDto> findByUserId(String authHeader) throws Exception {
    String userId = JwtProvider.getIdFromToken(authHeader);
    User owner = userRepository.findById(userId).orElseThrow(() -> new Exception("User not found"));
    List<Space> spaces = spaceRepository.findByOwner(owner);
    List<SpaceDto> spaceDtos = new ArrayList<>();
    for (Space space : spaces) {
      SpaceDto spaceDto = mapSpaceToSpaceDto(space);
      spaceDtos.add(spaceDto);
    }
    return spaceDtos;
  }

  @Override
  @Transactional
  public SpaceDto deleteElement(String authHeader, String elementId) throws Exception {

    SpaceElement spaceElement = spaceElementRepository.findById(elementId).orElse(null);
    if (spaceElement == null) {
      throw new Exception("Element Not Found");
    }
    String userId = JwtProvider.getIdFromToken(authHeader);
    Space space = spaceRepository.findById(spaceElement.getSpace().getId()).orElse(null);
    if (space == null) {
      throw new Exception("Space Not Found");
    }
    if (!Objects.equals(userId, space.getOwner().getId())) {
      throw new Exception("Unauthorized");
    }
    Set<SpaceElement> spaceElements = space.getSpaceElements();
    SpaceElement seToDelete = null;
    for (SpaceElement se : spaceElements) {
      if (se.getId().equals(elementId)) {
        seToDelete = se;
      }
    }

    if (seToDelete != null) {
      spaceElements.remove(seToDelete);
    }
    space.setSpaceElements(spaceElements);
    space = spaceRepository.save(space);
    // spaceElementRepository.deleteById(spaceElement.getId());
    spaceRepository.flush();
    entityManager.clear();
    return mapSpaceToSpaceDto(space);
  }

  public SpaceElement mapSpaceElementDtoToSpaceElement(SpaceElementDto spaceElementDto, Space spaceEntity)
      throws Exception {
    Element element = elementRepository.findById(spaceElementDto.getElementId())
        .orElseThrow(() -> new Exception("Element Not Found"));

    return SpaceElement.builder()
        .space(spaceEntity)
        .x(spaceElementDto.getX())
        .y(spaceElementDto.getY())
        .element(element)
        .build();
  }

  public SpaceElement mapMapElementDtoToSpaceElement(MapElement mapElement, Space spaceEntity) {
    Element element = mapElement.getElement();

    return SpaceElement.builder()
        .space(spaceEntity)
        .x(mapElement.getX())
        .y(mapElement.getY())
        .element(element)
        .build();
  }

  public SpaceDto mapSpaceToSpaceDto(Space spaceEntity) {
    SpaceDto spaceDto = SpaceDto.builder()
        .mapId(spaceEntity.getGameMap() == null ? null : spaceEntity.getGameMap().getId())
        .name(spaceEntity.getName())
        .dimensions(spaceEntity.getHeight() + "x" + spaceEntity.getWidth())
        .thumbnail(spaceEntity.getThumbnail())
        .ownerId(spaceEntity.getOwner().getId())
        .id(spaceEntity.getId())
        .build();
    List<SpaceElementDto> spaceElementDtos = new ArrayList<>();
    List<SpaceElement> spaceElements = jdbc.query(
        "SELECT se.id AS space_element_id, se.space_id, se.element_id, se.x, se.y, " +
            "e.id AS element_id, e.height, e.width, e.image_url, e.is_static " +
            "FROM space_element se " +
            "JOIN element e ON se.element_id = e.id " +
            "WHERE se.space_id = ?",
        new Object[] { spaceEntity.getId() },
        (rs, rowNum) -> {
          SpaceElement spaceElement = new SpaceElement();
          spaceElement.setId(rs.getString("space_element_id"));
          spaceElement.setX(rs.getInt("x"));
          spaceElement.setY(rs.getInt("y"));

          Element element = new Element();
          element.setId(rs.getString("element_id"));
          element.setHeight(rs.getInt("height"));
          element.setWidth(rs.getInt("width"));
          element.setImageUrl(rs.getString("image_url"));
          element.setStatic(rs.getBoolean("is_static"));

          spaceElement.setElement(element);
          return spaceElement;
        });

    for (SpaceElement spaceElement : spaceElements) {
      SpaceElementDto spaceElementDto = mapSpaceElementToSpaceElementDto(spaceElement, spaceEntity);
      spaceElementDtos.add(spaceElementDto);
    }
    spaceDto.setElements(spaceElementDtos);
    return spaceDto;
  }

  public SpaceElementDto mapSpaceElementToSpaceElementDto(SpaceElement spaceElement) {
    return SpaceElementDto.builder()
        .y(spaceElement.getY())
        .x(spaceElement.getX())
        .spaceId(spaceElement.getSpace().getId())
        .isStatic(spaceElement.getElement().isStatic())
        .elementId(spaceElement.getId())
        .build();
  }

  public SpaceElementDto mapSpaceElementToSpaceElementDto(SpaceElement spaceElement, Space spaceEntity) {
    return SpaceElementDto.builder()
        .y(spaceElement.getY())
        .x(spaceElement.getX())
        .spaceId(spaceEntity.getId())
        .isStatic(spaceElement.getElement().isStatic())
        .id(spaceElement.getId())
        .build();
  }
}
