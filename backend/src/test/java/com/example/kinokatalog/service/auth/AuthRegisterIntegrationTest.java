package com.example.kinokatalog.service.auth;


import com.example.kinokatalog.persistence.sql.entity.UserEntity;
import com.example.kinokatalog.persistence.sql.repository.UserSqlRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@Tag("integration")
class AuthRegisterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private UserSqlRepository userRepo;

    private String body(String email, String username, String password) {
        return """
        {
          "email": "%s",
          "username": "%s",
          "password": "%s"
        }
        """.formatted(email, username, password);
    }

    @Test
    void register_success_persistsUser() throws Exception {
        mockMvc.perform(
                        post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body("john@mail.com", "john123", "Aa1!abcd"))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john123"));

        assertTrue(userRepo.existsByUsername("john123"));
    }

    @Test
    void duplicateUsername_returnsConflict() throws Exception {
        userRepo.save(new UserEntity(null, "john123", "x@mail.com", "Hishis", null,"USER",null, "unused"));

        mockMvc.perform(
                        post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body("y@mail.com", "john123", "Aa1!abcd"))
                )
                .andExpect(status().isConflict());
    }

    @Test
    void duplicateEmail_returnsConflict() throws Exception {
        userRepo.save(new UserEntity(null, "userA", "dup@mail.com","hashed", null,"USER",null, "unused"));

        mockMvc.perform(
                        post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body("dup@mail.com", "userB", "Aa1!abcd"))
                )
                .andExpect(status().isConflict());
    }

    @Test
    void malformedJson_returnsBadRequest() throws Exception {
        mockMvc.perform(
                        post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ bad json }")
                )
                .andExpect(status().isBadRequest());
    }
}
