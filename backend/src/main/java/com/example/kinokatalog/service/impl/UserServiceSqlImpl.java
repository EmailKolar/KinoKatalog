package com.example.kinokatalog.service.impl;

import com.example.kinokatalog.controller.UserController;
import com.example.kinokatalog.dto.RegisterRequest;
import com.example.kinokatalog.dto.UserDTO;
import com.example.kinokatalog.exception.ConflictException;
import com.example.kinokatalog.exception.InvalidDataException;
import com.example.kinokatalog.mapper.UserMapper;
import com.example.kinokatalog.persistence.sql.entity.UserEntity;
import com.example.kinokatalog.persistence.sql.repository.UserSqlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.kinokatalog.service.PasswordValidator.isValidPassword;

@Service
@RequiredArgsConstructor
public class UserServiceSqlImpl {

    private final UserSqlRepository userSqlRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;



    public UserDTO getUserById(Integer id) {
        return userSqlRepository.findById(id).map(UserMapper::toUserDTO).orElseThrow(() -> new RuntimeException("User not found"));

    }

    public UserDTO getUserByUsername(String username) {
        return userSqlRepository.findByUsername(username).map(UserMapper::toUserDTO).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserDTO register(RegisterRequest req){
        req.setUsername(req.getUsername().trim());
        req.setEmail(req.getEmail().trim());

        if (!isValidEmail(req.getEmail())) {
            throw new InvalidDataException("Invalid email format");
        }

        if (!isValidUsername(req.getUsername())) {
            throw new InvalidDataException("Invalid username format");
        }
        if (!isValidPassword(req.getPassword(), req.getUsername(), req.getEmail())) {
            throw new InvalidDataException("Invalid password format");
        }

        if (userSqlRepository.existsByUsername(req.getUsername())) {
            throw new ConflictException("Username already exists");
        }
        if(userSqlRepository.existsByEmail(req.getEmail())){
            throw new ConflictException("Email already exists");
        }

        UserEntity newUser = new UserEntity();
        newUser.setUsername(req.getUsername());
        newUser.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        newUser.setEmail(req.getEmail());
        newUser.setRole("USER");
        UserEntity savedUser = userSqlRepository.save(newUser);
        return UserMapper.toUserDTO(savedUser);
    }

    boolean isValidEmail(String email) {
        if (email == null) return false;

        if (email.length() > 254) return false;
        if (!email.contains("@")) return false;

        String[] parts = email.split("@", -1);
        if (parts.length != 2) return false;

        String local = parts[0];
        String domain = parts[1];

        if (local.isEmpty() || domain.isEmpty()) return false;
        if (local.length() > 64) return false;
        if (email.contains("..")) return false;

        // Simple allowed chars (your regex)
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+$"))
            return false;

        // domain must contain dot
        if (!domain.contains(".")) return false;

        // domain-label rules
        for (String label : domain.split("\\.")) {
            if (label.isEmpty()) return false;
            if (label.length() > 63) return false;
            if (label.startsWith("-") || label.endsWith("-")) return false;
        }

        return true;
    }


    boolean isValidUsername(String username){
        String usernameRegex = "^[A-Za-z0-9_-]{3,20}$";
        return username != null && username.matches(usernameRegex);
    }







}
