package com.example.kinokatalog.controller;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Tag("integration")
class ProfileUploadIntegrationTest {

    @Autowired MockMvc mockMvc;

    private String jwtFor(String username) {
        return "Bearer " + username; // matches how your filter works
    }

    @Test
    void upload_success() throws Exception {

        byte[] validPng = Base64.getDecoder().decode(
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGNgYAAAAAMAASsJTYQAAAAASUVORK5CYII="
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                "image/png",
                validPng
        );

        mockMvc.perform(multipart("/api/users/upload")
                        .file(file)
                        .with(csrf())
                        .header("Authorization", jwtFor("user1"))
                        .with(user("user1"))
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Profile picture accepted!"));
    }

/*
    @Test
    void upload_forbidden_403() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                "image/png",
                new byte[]{1, 2, 3}
        );

        mockMvc.perform(multipart("/api/users/upload")
                        .file(file)
                        .with(csrf())
                )
                .andExpect(status().isForbidden());
    }*/

    @Test
    void upload_badMime_400() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "virus.exe",
                "application/octet-stream",
                new byte[]{1,2,3}
        );

        mockMvc.perform(multipart("/api/users/upload")
                        .file(file)
                        .with(csrf())
                        .header("Authorization", jwtFor("user1"))
                        .with(user("user1"))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid content type"));
    }

    @Test
    void upload_invalidImage_400() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "broken.png",
                "image/png",
                new byte[]{0,0,0,0} // not a real image
        );

        mockMvc.perform(multipart("/api/users/upload")
                        .file(file)
                        .with(csrf())
                        .header("Authorization", jwtFor("user1"))
                        .with(user("user1"))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("File is not a valid image"));
    }
}

