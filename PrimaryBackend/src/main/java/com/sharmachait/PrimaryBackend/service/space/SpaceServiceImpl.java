package com.sharmachait.PrimaryBackend.service.space;
import com.sharmachait.PrimaryBackend.config.jwt.JwtProvider;
import com.sharmachait.PrimaryBackend.models.dto.SpaceDto;
import com.sharmachait.PrimaryBackend.models.dto.SpaceElementDto;
import com.sharmachait.PrimaryBackend.models.entity.*;
import com.sharmachait.PrimaryBackend.repository.ElementRepository;
import com.sharmachait.PrimaryBackend.repository.SpaceRepository;
import com.sharmachait.PrimaryBackend.service.element.ElementService;
import com.sharmachait.PrimaryBackend.service.gameMap.GameMapService;
import com.sharmachait.PrimaryBackend.service.spaceElement.SpaceElementService;
import com.sharmachait.PrimaryBackend.service.user.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SpaceServiceImpl implements SpaceService {

    private final SpaceRepository spaceRepository;
    private final GameMapService gameMapService;
    private final UserService userService;
    private final ElementService elementService;
    private final SpaceElementService spaceElementService;
    private final ElementRepository elementRepository;

    @Override
    public Space findById(String id) throws NoSuchElementException {
        return spaceRepository.findById(id).get();
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

    @Transactional
    public SpaceElement mapToSpaceElement(SpaceElementDto spaceElementDto, Space spaceEntity) {
        Element element = elementService.getElementById(spaceElementDto.getElementId());

        SpaceElement spaceElement = SpaceElement.builder()
                .space(spaceEntity)
                .x(spaceElementDto.getX())
                .y(spaceElementDto.getY())
                .isStatic(spaceElementDto.isStatic())
                .element(element)
                .build();

        return spaceElement;
    }

    @Transactional
    public SpaceElement mapToSpaceElement(MapElement mapElement, Space spaceEntity) {
        Element element = mapElement.getElement();

        SpaceElement spaceElement = SpaceElement.builder()
                .space(spaceEntity)
                .x(mapElement.getX())
                .y(mapElement.getY())
                .isStatic(mapElement.isStatic())
                .element(element)
                .build();


        return spaceElement;
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
        if(userId!=spaceEntity.getOwner().getId()){
            throw new Exception("Unauthorized");
        }
        SpaceElement spaceElement = mapToSpaceElement(spaceElementDto, spaceEntity);
        spaceElement = spaceElementService.save(spaceElement);
        spaceEntity.getSpaceElements().add(spaceElement);
        return spaceRepository.save(spaceEntity);
    }

    @Override
    public List<Space> findByUserId(String authHeader) {
        String userId = JwtProvider.getIdFromToken(authHeader);
        return spaceRepository.findByOwnerId(userId);
    }

    public SpaceDto mapSpaceToSpaceDto(Space spaceEntity) {
        SpaceDto spaceDto = SpaceDto.builder()
                .mapId(spaceEntity.getGameMap().getId())
                .name(spaceEntity.getName())
                .dimensions(spaceEntity.getHeight()+"x"+spaceEntity.getWidth())
                .thumbnail(spaceEntity.getThumbnail())
                .id(spaceEntity.getId())
                .build();
        return spaceDto;
    }


}
