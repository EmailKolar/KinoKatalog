package com.example.kinokatalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String username;
    private String email;
    private String fullName;
    private Boolean isVerified;
    private String role;
    private LocalDateTime createdAt;
    private String profileImageUrl;
}
