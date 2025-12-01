package com.example.KinoKatalog.controller;

import com.example.KinoKatalog.config.JwtUtil;
import com.example.KinoKatalog.dto.UserDTO;
import com.example.KinoKatalog.service.impl.UserServiceSqlImpl;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserServiceSqlImpl userService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserServiceSqlImpl userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );

            var roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

            // try to obtain numeric userId from the authenticated principal (if custom UserDetails provides it)
            Integer userId = null;
            Object principal = auth.getPrincipal();
            if (principal != null) {
                try {
                    // attempt to call getId() reflectively (works if your UserDetails implementation exposes id)
                    var m = principal.getClass().getMethod("getId");
                    Object idObj = m.invoke(principal);
                    if (idObj instanceof Number) userId = ((Number) idObj).intValue();
                } catch (NoSuchMethodException ignored) {
                    // no getId on principal -> fallback below
                }
            }

            // fallback: resolve by username (keeps compatibility)
            if (userId == null) {
                try {
                    UserDTO user = userService.getUserByUsername(req.getUsername());
                    userId = user.getId();
                } catch (RuntimeException e) {
                    // user not found in DB -> authentication succeeded (maybe from in-memory), but no DB user to attach => reject
                    return ResponseEntity.status(401).build();
                }
            }

            String token = jwtUtil.generateToken(req.getUsername(), roles, userId);

            return ResponseEntity.ok(new LoginResponse(token, req.getUsername(), roles, userId));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).build();
        } catch (Exception ex) {
            return ResponseEntity.status(500).build();
        }
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    public static class LoginResponse {
        private final String token;
        private final String username;
        private final List<String> roles;
        private final Integer userId;
    }
}