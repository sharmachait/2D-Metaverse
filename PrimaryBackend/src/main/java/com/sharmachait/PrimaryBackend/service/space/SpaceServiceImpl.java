package com.sharmachait.PrimaryBackend.service.space;
import com.sharmachait.PrimaryBackend.config.jwt.JwtProvider;
import com.sharmachait.PrimaryBackend.models.dto.SpaceDto;
import com.sharmachait.PrimaryBackend.models.dto.SpaceElementDto;
import com.sharmachait.PrimaryBackend.models.entity.*;
import com.sharmachait.PrimaryBackend.repository.ElementRepository;
import com.sharmachait.PrimaryBackend.repository.SpaceElementRepository;
import com.sharmachait.PrimaryBackend.repository.SpaceRepository;
import com.sharmachait.PrimaryBackend.service.element.ElementService;
import com.sharmachait.PrimaryBackend.service.gameMap.GameMapService;
import com.sharmachait.PrimaryBackend.service.spaceElement.SpaceElementService;
import com.sharmachait.PrimaryBackend.service.user.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
@RequiredArgsConstructor
public class SpaceServiceImpl implements SpaceService {

    private final SpaceRepository spaceRepository;
    private final GameMapService gameMapService;
    private final UserService userService;
    private final ElementService elementService;
    private final SpaceElementService spaceElementService;
    private final ElementRepository elementRepository;
    private final SpaceElementRepository spaceElementRepository;

    @Override
    public SpaceDto findById(String id) throws NoSuchElementException {
        Space space =  spaceRepository.findById(id).get();
        SpaceDto spaceDto = mapSpaceToSpaceDto(space);
        return spaceDto;
    }

    @Transactional
    @Override
    public Space save(String authHeader, SpaceDto spaceDto) {
        int xIndex = spaceDto.getDimensions().indexOf('x');
        String height = spaceDto.getDimensions().substring(0,xIndex);
        String width = spaceDto.getDimensions().substring(xIndex+1);
        GameMap gameMap;
        if(spaceDto.getMapId() == null) {
            gameMap = null;
        }else{
            gameMap = gameMapService.findById(spaceDto.getMapId());

            if(gameMap == null) {
                throw new NoSuchElementException();
            }
        }

        Set<SpaceElement> spaceElements = new HashSet<>();
        String userId = JwtProvider.getIdFromToken(authHeader);
        User owner = userService.findById(userId);

        Space spaceEntity = Space.builder()
                .name(spaceDto.getName())
                .height(Integer.parseInt(height))
                .width(Integer.parseInt(width))
                .thumbnail(spaceDto.getThumbnail())
                .owner(owner)
                .build();

        if(gameMap != null) {
            gameMap.getSpaces().add(spaceEntity);
            gameMap = gameMapService.save(gameMap);
            spaceEntity.setHeight(gameMap.getHeight());
            spaceEntity.setWidth(gameMap.getWidth());
            for(MapElement mapElement: gameMap.getMapElements()){
                SpaceElement spaceElement = mapToSpaceElement(mapElement, spaceEntity);
                spaceElement = spaceElementService.save(spaceElement);
                spaceElements.add(spaceElement);
            }
        }

        spaceEntity.setGameMap(gameMap);
//        for(SpaceElementDto spaceElementDto : spaceDto.getElements()){
//            spaceElements.add(mapToSpaceElement(spaceElementDto, spaceEntity));
//        }

        spaceEntity.setSpaceElements(spaceElements);
        return spaceRepository.save(spaceEntity);
    }

    @Transactional
    @Override
    public void deleteById(String authHeader, String spaceId) throws Exception, NoSuchElementException {
        String userId = JwtProvider.getIdFromToken(authHeader);
        Space spaceEntity = spaceRepository.findById(spaceId).get();

        if(spaceEntity.getOwner().getId().equals(userId)) {
            spaceRepository.deleteById(spaceId);
        }
        else{
            throw new Exception("Unauthorized");
        }
    }

    @Override
    public Space save(Space space) {
        return spaceRepository.save(space);
    }

    @Override
    @Transactional
    public Space addElement(String authHeader, SpaceElementDto spaceElementDto, String spaceId) throws Exception {
        String userId = JwtProvider.getIdFromToken(authHeader);

        Space spaceEntity = spaceRepository.findById(spaceId).orElse(null);
        if(spaceEntity==null){
            throw new Exception("Space not found");
        }
        if(!Objects.equals(userId, spaceEntity.getOwner().getId())){
            throw new Exception("Unauthorized");
        }
        SpaceElement spaceElement = mapToSpaceElement(spaceElementDto, spaceEntity);
        spaceElement = spaceElementService.save(spaceElement);
        spaceEntity.getSpaceElements().add(spaceElement);
        return spaceRepository.save(spaceEntity);
    }

    @Override
    public List<SpaceDto> findByUserId(String authHeader) {
        String userId = JwtProvider.getIdFromToken(authHeader);
        User owner = userService.findById(userId);
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
    public Space deleteElement(String authHeader, String elementId) throws Exception {

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
        return space;
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
            SpaceElementDto spaceElementDto = SpaceElementDto.builder()
                    .y(spaceElement.getY())
                    .x(spaceElement.getX())
                    .isStatic(spaceElement.getElement().isStatic())
                    .elementId(spaceElement.getId())
                    .build();
            spaceElementDtos.add(spaceElementDto);
        }
        spaceDto.setElements(spaceElementDtos);
        return spaceDto;
    }
    public SpaceElement mapToSpaceElement(SpaceElementDto spaceElementDto, Space spaceEntity) throws Exception {
        Element element = elementService.getElementById(spaceElementDto.getElementId());

        SpaceElement spaceElement = SpaceElement.builder()
                .space(spaceEntity)
                .x(spaceElementDto.getX())
                .y(spaceElementDto.getY())
//                .isStatic(element.isStatic())
                .element(element)
                .build();

        return spaceElement;
    }


    public SpaceElement mapToSpaceElement(MapElement mapElement, Space spaceEntity) {
        Element element = mapElement.getElement();

        SpaceElement spaceElement = SpaceElement.builder()
                .space(spaceEntity)
                .x(mapElement.getX())
                .y(mapElement.getY())
//                .isStatic(element.isStatic())
                .element(element)
                .build();


        return spaceElement;
    }
}
