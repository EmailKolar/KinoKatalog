package com.example.kinokatalog.service.auth;


import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class LoginResult {
    private boolean success;
    private String username;
    private List<String> roles;
    private Integer userId;
    private String token;
    private String error;

    public static LoginResult success(String username, List<String> roles, Integer userId, String token) {
        return new LoginResult(true, username, roles, userId, token, null);
    }

    public static LoginResult failure(String error) {
        return new LoginResult(false, null, null, null, null, error);
    }
}

