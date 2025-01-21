package com.sharmachait.PrimaryBackend.service.avatar;

import com.sharmachait.PrimaryBackend.models.dto.AvatarDto;
import com.sharmachait.PrimaryBackend.models.entity.Avatar;
import com.sharmachait.PrimaryBackend.repository.AvatarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {
  private final AvatarRepository avatarRepository;

  @Override
  public AvatarDto findById(String id) throws NoSuchElementException {
    return mapAvatarToAvatarDto(
        avatarRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Avatar not found")));
  }

  @Override
  public AvatarDto save(Avatar avatar) {

    return mapAvatarToAvatarDto(avatarRepository.save(avatar));
  }

  @Override
  public AvatarDto save(AvatarDto avatar) {
    return save(mapDtoToAvatar(avatar));
  }

  @Override
  public List<AvatarDto> findAll() {
    return avatarRepository.findAll()
        .stream()
        .map((x) -> mapAvatarToAvatarDto(x))
        .collect(Collectors.toList());
  }

  public Avatar mapDtoToAvatar(AvatarDto dto) {
    return Avatar.builder()
        .name(dto.getName())
        .imageUrl(dto.getImageUrl())
        .build();
  }

  public AvatarDto mapAvatarToAvatarDto(Avatar avatar) {
    return AvatarDto.builder()
        .name(avatar.getName())
        .imageUrl(avatar.getImageUrl())
        .id(avatar.getId())
        .build();
  }
}
