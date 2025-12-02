package com.example.kinokatalog.service.impl;

import com.example.kinokatalog.dto.UserDTO;
import com.example.kinokatalog.mapper.UserMapper;
import com.example.kinokatalog.persistence.sql.repository.UserSqlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceSqlImpl {

    private final UserSqlRepository userSqlRepository;

    public UserDTO getUserById(Integer id) {
        return userSqlRepository.findById(id).map(UserMapper::toUserDTO).orElseThrow(() -> new RuntimeException("User not found"));

    }

    public UserDTO getUserByUsername(String username) {
        return userSqlRepository.findByUsername(username).map(UserMapper::toUserDTO).orElseThrow(() -> new RuntimeException("User not found"));


    }
}
