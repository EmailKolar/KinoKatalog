package com.example.kinokatalog.mapper;

import com.example.kinokatalog.dto.UserDTO;

import com.example.kinokatalog.persistence.sql.entity.UserEntity;

public class UserMapper {

    public static UserDTO toUserDTO(UserEntity userEntity, String publicUrl) {
        if (userEntity == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(userEntity.getId());
        dto.setUsername(userEntity.getUsername());
        dto.setFullName(userEntity.getFullName());
        dto.setEmail(userEntity.getEmail());
        dto.setIsVerified(userEntity.getIsVerified());
        dto.setRole(userEntity.getRole());
        dto.setCreatedAt(userEntity.getCreatedAt());
        dto.setProfileImageUrl(publicUrl);
        return dto;
    }

    public static UserEntity toUserEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userDTO.getId());
        userEntity.setUsername(userDTO.getUsername());
        userEntity.setFullName(userDTO.getFullName());
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setIsVerified(userDTO.getIsVerified());
        userEntity.setRole(userDTO.getRole());
        userEntity.setCreatedAt(userDTO.getCreatedAt());
        return userEntity;
    }
}
