package com.example.kinokatalog.config;


import com.example.kinokatalog.persistence.sql.entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class SecurityUser extends User {

    private final Integer id;

    public SecurityUser(UserEntity user) {
        super(
                user.getUsername(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
        this.id = user.getId();
    }

}