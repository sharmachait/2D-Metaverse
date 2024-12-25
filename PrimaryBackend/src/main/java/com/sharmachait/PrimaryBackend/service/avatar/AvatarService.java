package com.sharmachait.PrimaryBackend.service.avatar;

import com.sharmachait.PrimaryBackend.models.entity.Avatar;
import com.sharmachait.PrimaryBackend.models.entity.User;

import java.util.NoSuchElementException;

public interface AvatarService {
    Avatar findById(String id) throws NoSuchElementException;
    Avatar save(Avatar avatar);
}
