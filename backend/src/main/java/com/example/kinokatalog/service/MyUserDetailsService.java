package com.example.kinokatalog.service;

import com.example.kinokatalog.config.SecurityUser;
import com.example.kinokatalog.persistence.sql.entity.UserEntity;
import com.example.kinokatalog.persistence.sql.repository.UserSqlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserSqlRepository repo;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        UserEntity user = repo.findByUsername(identifier)
                .or(() -> repo.findByEmail(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new SecurityUser(user);
    }


}

