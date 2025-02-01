package com.sharmachait.PrimaryBackend.service.spaceElement;

import com.sharmachait.PrimaryBackend.models.dto.SpaceElementDto;
import com.sharmachait.PrimaryBackend.models.entity.SpaceElement;

public interface SpaceElementService {
  SpaceElementDto save(SpaceElement spaceElement);
}
