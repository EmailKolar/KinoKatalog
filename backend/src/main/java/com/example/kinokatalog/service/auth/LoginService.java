package com.example.kinokatalog.service.auth;

import com.example.kinokatalog.config.JwtUtil;
import com.example.kinokatalog.config.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private boolean hasUnsafeCharacters(String s) {
        if (s == null) return true;
        for (char c : s.toCharArray()) {
            if (c <= 31 || c == 127) { // control characters + DEL
                return true;
            }
        }
        return false;
    }

    public LoginResult login(String identifier, String password) {

        //validation

        if (identifier == null || identifier.isBlank()) {
            return LoginResult.failure("INVALID_IDENTIFIER");
        }

        if (identifier.length() > 255) {
            return LoginResult.failure("INVALID_IDENTIFIER");
        }

        if (hasUnsafeCharacters(identifier)) {
            return LoginResult.failure("INVALID_IDENTIFIER");
        }

        if (password == null || password.isEmpty()) {
            return LoginResult.failure("INVALID_PASSWORD");
        }

        if (password.length() > 128) {
            return LoginResult.failure("INVALID_PASSWORD");
        }

        if (hasUnsafeCharacters(password)) {
            return LoginResult.failure("INVALID_PASSWORD");
        }

        // Authenticate user

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(identifier, password)
            );

            SecurityUser user = (SecurityUser) auth.getPrincipal();

            Collection<? extends GrantedAuthority> auths = auth.getAuthorities();
            List<String> roles = auths.stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            String token = jwtUtil.generateToken(
                    user.getUsername(),
                    roles,
                    user.getId()
            );

            return LoginResult.success(
                    user.getUsername(),
                    roles,
                    user.getId(),
                    token
            );

        } catch (BadCredentialsException ex) {
            return LoginResult.failure("INVALID_CREDENTIALS");
        } catch (Exception ex) {
            return LoginResult.failure("SERVER_ERROR");
        }
    }
}