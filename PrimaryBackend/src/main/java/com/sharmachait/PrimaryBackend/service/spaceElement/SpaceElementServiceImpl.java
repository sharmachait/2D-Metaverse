package com.sharmachait.PrimaryBackend.service.spaceElement;

import com.sharmachait.PrimaryBackend.models.dto.SpaceElementDto;
import com.sharmachait.PrimaryBackend.models.entity.SpaceElement;
import com.sharmachait.PrimaryBackend.repository.SpaceElementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SpaceElementServiceImpl implements SpaceElementService {
  private final SpaceElementRepository spaceElementRepository;

  @Override
  public SpaceElementDto save(SpaceElement spaceElement) {
    return mapSpaceElementToDto(spaceElementRepository.save(spaceElement));
  }

  private SpaceElementDto mapSpaceElementToDto(SpaceElement spaceElement) {
    SpaceElementDto spaceElementDto = new SpaceElementDto();
    spaceElementDto.setId(spaceElement.getId());
    spaceElementDto.setElementId(spaceElement.getElement().getId());
    spaceElementDto.setX(spaceElement.getX());
    spaceElementDto.setY(spaceElement.getY());
    spaceElementDto.setStatic(spaceElement.getElement().isStatic());
    return spaceElementDto;
  }
}
