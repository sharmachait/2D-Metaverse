package com.sharmachait.PrimaryBackend.service.element;

import com.sharmachait.PrimaryBackend.models.dto.ElementDto;
import com.sharmachait.PrimaryBackend.models.entity.Element;

public interface ElementService {
    Element getElementById(String id) throws Exception;
    ElementDto save(Element element);
    ElementDto save(ElementDto elementDto);
    ElementDto update(String elementId, ElementDto elementDto) throws Exception;
}
