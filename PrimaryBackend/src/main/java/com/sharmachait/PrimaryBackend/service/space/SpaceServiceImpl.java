package com.sharmachait.PrimaryBackend.service.space;
import com.sharmachait.PrimaryBackend.config.jwt.JwtProvider;
import com.sharmachait.PrimaryBackend.models.dto.GameMapDto;
import com.sharmachait.PrimaryBackend.models.dto.SpaceDto;
import com.sharmachait.PrimaryBackend.models.dto.SpaceElementDto;
import com.sharmachait.PrimaryBackend.models.entity.*;
import com.sharmachait.PrimaryBackend.repository.*;
import com.sharmachait.PrimaryBackend.service.element.ElementService;
import com.sharmachait.PrimaryBackend.service.gameMap.GameMapService;
import com.sharmachait.PrimaryBackend.service.spaceElement.SpaceElementService;
import com.sharmachait.PrimaryBackend.service.user.UserService;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.usertype.ParameterizedType;
import org.modelmapper.internal.bytebuddy.asm.Advice;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
@RequiredArgsConstructor
public class SpaceServiceImpl implements SpaceService {

    private final SpaceRepository spaceRepository;
    private final GameMapService gameMapService;
    private final UserRepository userRepository;
    private final ElementService elementService;
    private final SpaceElementService spaceElementService;
    private final ElementRepository elementRepository;
    private final SpaceElementRepository spaceElementRepository;
    private final MapElementRepository mapElementRepository;
    private final GameMapRepository gameMapRepository;
    private final JdbcTemplate jdbc;

    @Transactional
    @Override
    public void deleteById(String authHeader, String spaceId) throws Exception {
        String userId = JwtProvider.getIdFromToken(authHeader);
        Space spaceEntity = spaceRepository.findById(spaceId).orElseThrow(()-> new Exception("Space not found"));

        if(spaceEntity.getOwner().getId().equals(userId)) {
            spaceRepository.deleteById(spaceId);
        }
        else{
            throw new Exception("Unauthorized");
        }
    }

    @Override
    public SpaceDto findById(String id) throws NoSuchElementException {
        Space space =  spaceRepository.findById(id).orElseThrow(()-> new NoSuchElementException("Space not found"));
        return mapSpaceToSpaceDto(space);
    }


    @Transactional
    @Override
    public SpaceDto save(String ownerID, String gameMapId, SpaceDto spaceDto) throws Exception {
        User owner = userRepository.findById(ownerID)
                .orElseThrow(() -> new Exception("User not found"));

        int xIndex = spaceDto.getDimensions().indexOf('x');
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

            List<MapElement> mapElements = jdbc.query(
                    "select * from map_element where map_id = ?",
                    new Object[]{gameMapId},
                    new BeanPropertyRowMapper<>(MapElement.class)
            );
            Set<SpaceElement> spaceElements = new HashSet<>();
            for (MapElement mapElement : mapElements) {
                SpaceElement spaceElement = mapMapElementDtoToSpaceElement(mapElement, spaceEntity);
                spaceElementRepository.save(spaceElement);
                spaceElements.add(spaceElement);
            }

            spaceEntity.setThumbnail(spaceDto.getThumbnail() != null ?
                    spaceDto.getThumbnail() : gameMap.getThumbnail());

            spaceEntity.setGameMap(gameMap);
        }

        Space savedSpace = spaceRepository.save(spaceEntity);
        savedSpace = spaceRepository.findById(savedSpace.getId()).orElseThrow(()-> new Exception("Space not found"));
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
        if(spaceEntity==null){
            throw new Exception("Space not found");
        }
        if(!Objects.equals(userId, spaceEntity.getOwner().getId())){
            throw new Exception("Unauthorized");
        }
        SpaceElement spaceElement = mapSpaceElementDtoToSpaceElement(spaceElementDto, spaceEntity);
        spaceElement = spaceElementRepository.save(spaceElement);
        spaceEntity.getSpaceElements().add(spaceElement);
        return mapSpaceToSpaceDto(spaceRepository.save(spaceEntity));
    }

    @Override
    public List<SpaceDto> findByUserId(String authHeader) throws Exception {
        String userId = JwtProvider.getIdFromToken(authHeader);
        User owner = userRepository.findById(userId).orElseThrow(()-> new Exception("User not found"));
        List<Space> spaces = spaceRepository.findByOwner(owner);
        List<SpaceDto> spaceDtos = new ArrayList<>();
        for(Space space: spaces){
            SpaceDto spaceDto = mapSpaceToSpaceDto(space);
            spaceDtos.add(spaceDto);
        }
        return spaceDtos;
    }

    @Override
    @Transactional
    public SpaceDto deleteElement(String authHeader, String elementId) throws Exception {

        SpaceElement spaceElement = spaceElementRepository.findById(elementId).orElse(null);
        if(spaceElement == null){
            throw new Exception("Element Not Found");
        }
        String userId = JwtProvider.getIdFromToken(authHeader);
        Space space = spaceRepository.findById(spaceElement.getSpace().getId()).orElse(null);
        if(space == null){
            throw new Exception("Space Not Found");
        }
        if(!Objects.equals(userId, space.getOwner().getId())) {
            throw new Exception("Unauthorized");
        }
        for(SpaceElement se : space.getSpaceElements()){
            if(se.getId().equals(spaceElement.getId())){
                spaceElement=se;
                break;
            }
        }
        space.getSpaceElements().remove(spaceElement);
        spaceElementRepository.delete(spaceElement);
        space = spaceRepository.save(space);
        return mapSpaceToSpaceDto(space);
    }


    public SpaceElement mapSpaceElementDtoToSpaceElement(SpaceElementDto spaceElementDto, Space spaceEntity) throws Exception {
        Element element = elementRepository.findById(spaceElementDto.getElementId()).orElseThrow(()-> new Exception("Element Not Found"));

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
                .mapId(spaceEntity.getGameMap() == null ? null: spaceEntity.getGameMap().getId())
                .name(spaceEntity.getName())
                .dimensions(spaceEntity.getHeight()+"x"+spaceEntity.getWidth())
                .thumbnail(spaceEntity.getThumbnail())
                .ownerId(spaceEntity.getOwner().getId())
                .id(spaceEntity.getId())
                .build();
        List<SpaceElementDto> spaceElementDtos = new ArrayList<>();
        for(SpaceElement spaceElement : spaceEntity.getSpaceElements()){
            SpaceElementDto spaceElementDto = mapSpaceElementToSpaceElementDto(spaceElement);
            spaceElementDtos.add(spaceElementDto);
        }
        spaceDto.setElements(spaceElementDtos);
        return spaceDto;
    }

    public SpaceElementDto mapSpaceElementToSpaceElementDto(SpaceElement spaceElement){
        return SpaceElementDto.builder()
                .y(spaceElement.getY())
                .x(spaceElement.getX())
                .spaceId(spaceElement.getSpace().getId())
                .isStatic(spaceElement.getElement().isStatic())
                .elementId(spaceElement.getId())
                .build();
    }
}
