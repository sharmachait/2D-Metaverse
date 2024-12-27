package com.sharmachait.PrimaryBackend.service.element;

import com.sharmachait.PrimaryBackend.models.entity.Element;
import com.sharmachait.PrimaryBackend.repository.ElementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ElementServiceImpl implements ElementService {
    private final ElementRepository elementRepository;
    @Override
    public Element getElementById(String id) {
        return elementRepository.findById(id).orElse(null);
    }

    @Override
    public Element save(Element element) {
        return elementRepository.save(element);
    }
}
