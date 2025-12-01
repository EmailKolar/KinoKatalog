package com.example.KinoKatalog.service;

import com.example.KinoKatalog.persistence.sql.entity.UserEntity;

public interface UserService {

    UserEntity findUserById(Integer userId);
}
