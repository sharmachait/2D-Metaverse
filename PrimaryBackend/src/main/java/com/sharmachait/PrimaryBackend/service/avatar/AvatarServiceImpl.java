package com.sharmachait.PrimaryBackend.service.avatar;

import com.sharmachait.PrimaryBackend.models.entity.Avatar;
import com.sharmachait.PrimaryBackend.repository.AvatarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {
    private final AvatarRepository avatarRepository;
    @Override
    public Avatar findById(String id) throws NoSuchElementException {
        return avatarRepository.findById(id).get();
    }

    @Override
    public Avatar save(Avatar avatar) {
        return avatarRepository.save(avatar);
    }
}
