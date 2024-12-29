package com.sharmachait.PrimaryBackend.service.avatar;

import com.sharmachait.PrimaryBackend.models.dto.AvatarDto;
import com.sharmachait.PrimaryBackend.models.entity.Avatar;
import com.sharmachait.PrimaryBackend.models.entity.User;

import java.util.List;
import java.util.NoSuchElementException;

public interface AvatarService {
    AvatarDto findById(String id) throws NoSuchElementException;
    AvatarDto save(Avatar avatar);
    AvatarDto save(AvatarDto avatar);
    List<AvatarDto> findAll();
}
