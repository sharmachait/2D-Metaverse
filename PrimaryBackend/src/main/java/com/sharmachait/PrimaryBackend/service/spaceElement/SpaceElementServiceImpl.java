package com.sharmachait.PrimaryBackend.service.spaceElement;

import com.sharmachait.PrimaryBackend.models.entity.SpaceElement;
import com.sharmachait.PrimaryBackend.repository.SpaceElementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SpaceElementServiceImpl implements SpaceElementService {
    private final SpaceElementRepository spaceElementRepository;
    @Override
    public SpaceElement save(SpaceElement spaceElement) {
        return spaceElementRepository.save(spaceElement);
    }
}
