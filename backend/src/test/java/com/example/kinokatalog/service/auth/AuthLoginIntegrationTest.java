package com.example.kinokatalog.service.auth;

import com.example.kinokatalog.config.DataConfig;
import com.example.kinokatalog.persistence.sql.entity.UserEntity;
import com.example.kinokatalog.persistence.sql.repository.UserSqlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("integration")
@Transactional
@ImportAutoConfiguration(exclude = {DataConfig.class})
class AuthLoginIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserSqlRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    private String body(String identifier, String password) {
        return """
        {
          "identifier": "%s",
          "password": "%s"
        }
        """.formatted(identifier, password);
    }

    @BeforeEach
    void setup() {
        // Seed a real user into the test DB
        UserEntity user = new UserEntity();
        user.setUsername("john123");
        user.setEmail("john@mail.com");
        user.setPasswordHash(passwordEncoder.encode("Aa1!abcd"));
        user.setRole("USER");
        userRepo.save(user);
    }

    @Test
    void login_success_setsJwtCookieAndReturnsUser() throws Exception {
        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body("john123", "Aa1!abcd"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john123"))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(cookie().exists("authToken"))
                .andExpect(cookie().httpOnly("authToken", true))
                .andExpect(cookie().maxAge("authToken", greaterThan(0)))
                .andExpect(cookie().path("authToken", "/"));
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body("john123", "WrongPassword"))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_unknownUser_returns401() throws Exception {
        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body("nobody", "Aa1!abcd"))
                )
                .andExpect(status().isUnauthorized());
    }
}
