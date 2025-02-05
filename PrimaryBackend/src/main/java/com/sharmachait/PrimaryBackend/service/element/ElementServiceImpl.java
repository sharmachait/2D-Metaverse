package com.sharmachait.PrimaryBackend.service.element;

import com.sharmachait.PrimaryBackend.models.dto.ElementDto;
import com.sharmachait.PrimaryBackend.models.entity.Element;
import com.sharmachait.PrimaryBackend.repository.ElementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElementServiceImpl implements ElementService {
  private final ElementRepository elementRepository;

  @Override
  public ElementDto getElementById(String id) throws Exception {
    Element element = elementRepository
        .findById(id)
        .orElseThrow(() -> new Exception("Element not found"));
    return mapElementToDto(element);
  }

  @Override
  public ElementDto save(Element element) {
    return mapElementToDto(elementRepository.save(element));
  }

  @Override
  public ElementDto save(ElementDto elementDto) {
    return mapElementToDto(elementRepository.save(mapDtoToElement(elementDto)));
  }

  @Override
  public ElementDto update(String elementId, ElementDto elementDto) throws Exception {
    ElementDto element = getElementById(elementId);
    // element.setStatic(elementDto.getIsStatic());
    element.setImageUrl(elementDto.getImageUrl());
    return save(element);
  }

  @Override
  public List<ElementDto> getAllElements() {
    return elementRepository.findAll().stream().map(this::mapElementToDto).collect(Collectors.toList());
  }

  public ElementDto mapElementToDto(Element element) {
    return ElementDto.builder()
        .id(element.getId())
        .isStatic(element.isStatic())
        .width(element.getWidth())
        .height(element.getHeight())
        .imageUrl(element.getImageUrl())
        .build();
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
}
