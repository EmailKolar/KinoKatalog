package com.example.kinokatalog.service.impl;

import com.example.kinokatalog.dto.UserDTO;
import com.example.kinokatalog.mapper.UserMapper;
import com.example.kinokatalog.persistence.sql.entity.UserEntity;
import com.example.kinokatalog.persistence.sql.repository.UserSqlRepository;
import com.example.kinokatalog.service.ProfilePhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceSqlImpl {

    private final UserSqlRepository userSqlRepository;
    private final ProfilePhotoService profilePhotoService;


    public UserDTO getUserById(Integer id) {
        var entity = userSqlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String url = null;
        if (entity.getProfileImageKey() != null) {
            try {
                url = profilePhotoService.getPresignedUrl(entity.getProfileImageKey());
            } catch (Exception ignored) {}
        }

        return UserMapper.toUserDTO(entity, url);
    }

    public UserDTO getUserByUsername(String username) {
        var entity = userSqlRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String url = null;
        if (entity.getProfileImageKey() != null) {
            try {
                url = profilePhotoService.getPresignedUrl(entity.getProfileImageKey());
            } catch (Exception ignored) {}
        }

        return UserMapper.toUserDTO(entity, url);
    }

    @Transactional
    public void updateProfileImageKey(Integer id, String key) {
        UserEntity user = userSqlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setProfileImageKey(key);
        userSqlRepository.save(user);
    }
}
