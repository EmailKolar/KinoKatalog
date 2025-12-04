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



    private boolean isValidEmail(String email){
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"; //TODO match test reqs
        return email != null && email.matches(emailRegex);
    }

    private boolean isValidUsername(String username){
        String usernameRegex = "^[a-zA-Z0-9._-]{3,20}$"; //TODO match test reqs
        return username != null && username.matches(usernameRegex);
    }







}
