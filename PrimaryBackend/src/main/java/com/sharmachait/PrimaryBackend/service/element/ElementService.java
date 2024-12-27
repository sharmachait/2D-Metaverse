package com.sharmachait.PrimaryBackend.service.element;

import com.sharmachait.PrimaryBackend.models.entity.Element;

public interface ElementService {
    Element getElementById(String id);
    Element save(Element element);
}
