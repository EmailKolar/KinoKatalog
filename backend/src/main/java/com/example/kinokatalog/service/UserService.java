package com.example.kinokatalog.service;

import com.example.kinokatalog.persistence.sql.entity.UserEntity;

public interface UserService {

    UserEntity getUserById(Integer userId);
}
