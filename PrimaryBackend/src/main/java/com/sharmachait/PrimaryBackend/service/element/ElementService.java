package com.sharmachait.PrimaryBackend.service.element;

import com.sharmachait.PrimaryBackend.models.dto.ElementDto;
import com.sharmachait.PrimaryBackend.models.entity.Element;

import java.util.List;

public interface ElementService {
  ElementDto getElementById(String id) throws Exception;

  ElementDto save(Element element);

  ElementDto save(ElementDto elementDto);

  ElementDto update(String elementId, ElementDto elementDto) throws Exception;

  List<ElementDto> getAllElements();
}
