package com.example.kinokatalog.controller;

import com.example.kinokatalog.config.JwtUtil;
import com.example.kinokatalog.config.SecurityUser;
import com.example.kinokatalog.dto.UserDTO;
import com.example.kinokatalog.service.auth.LoginResult;
import com.example.kinokatalog.service.auth.LoginService;
import com.example.kinokatalog.service.impl.UserServiceSqlImpl;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserServiceSqlImpl userService;
    private final LoginService loginService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserServiceSqlImpl userService, LoginService loginService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.loginService = loginService;
    }
/*
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req, HttpServletResponse response) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getIdentifier(), req.getPassword())
            );

            var roles = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // Resolve userId
            Integer userId = null;
            Object principal = auth.getPrincipal();
            if (principal != null) {
                try {
                    var m = principal.getClass().getMethod("getId");
                    Object idObj = m.invoke(principal);
                    if (idObj instanceof Number) userId = ((Number) idObj).intValue();
                } catch (NoSuchMethodException ignored) {}
            }

            if (userId == null) {
                try {
                    UserDTO user = userService.getUserByUsername(req.getIdentifier());
                    userId = user.getId();
                } catch (RuntimeException e) {
                    return ResponseEntity.status(401).build();
                }
            }

            String token = jwtUtil.generateToken(req.getIdentifier(), roles, userId);

            // Set HttpOnly cookie instead of returning token in body
            Cookie cookie = new Cookie("authToken", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // Set to false for local dev without HTTPS
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            cookie.setAttribute("SameSite", "Lax"); // or "Strict"
            response.addCookie(cookie);

            // Return user info without token
            return ResponseEntity.ok(new LoginResponse(req.getIdentifier(), roles, userId));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).build();
        } catch (Exception ex) {
            return ResponseEntity.status(500).build();
        }
    }*/
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req, HttpServletResponse response) {

        LoginResult result = loginService.login(req.getIdentifier(), req.getPassword());

        if (!result.isSuccess()) {
            if ("INVALID_CREDENTIALS".equals(result.getError()))
                return ResponseEntity.status(401).build();
            return ResponseEntity.status(500).build();
        }

        // Set HttpOnly cookie
        Cookie cookie = new Cookie("authToken", result.getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);

        return ResponseEntity.ok(
                new LoginResponse(result.getUsername(), result.getRoles(), result.getUserId())
        );
    }



    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // Clear the cookie
        Cookie cookie = new Cookie("authToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @Data
    public static class LoginRequest {
        private String identifier;
        private String password;
    }

    @Data
    public static class LoginResponse {
        private final String username;
        private final List<String> roles;
        private final Integer userId;
    }


}