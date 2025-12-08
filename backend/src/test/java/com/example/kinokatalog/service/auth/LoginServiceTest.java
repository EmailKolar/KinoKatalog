package com.example.kinokatalog.service.auth;

import com.example.kinokatalog.config.JwtUtil;
import com.example.kinokatalog.config.SecurityUser;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;

import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoginServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private LoginService loginService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


    // HELPER: Build mock Authentication + SecurityUser
    private Authentication auth(String username, int id, String role) {
        SecurityUser principal = mock(SecurityUser.class);
        when(principal.getUsername()).thenReturn(username);
        when(principal.getId()).thenReturn(id);
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(principal);
        when(auth.getAuthorities())
                .thenReturn((Collection) List.of(new SimpleGrantedAuthority("ROLE_USER")));

        return auth;
    }

    // EP1: Valid identifier + correct password → SUCCESS
    @Test
    void loginSuccess() {
        Authentication authentication = auth("alice", 42, "ROLE_USER");

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(jwtUtil.generateToken("alice", List.of("ROLE_USER"), 42))
                .thenReturn("jwt-token");

        LoginResult result = loginService.login("alice", "goodpass");

        assertTrue(result.isSuccess());
        assertEquals("alice", result.getUsername());
        assertEquals(42, result.getUserId());
        assertEquals("jwt-token", result.getToken());
    }


    // EP2: Valid identifier but incorrect password → INVALID CREDENTIALS
    @Test
    void loginIncorrectPassword() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad"));

        LoginResult result = loginService.login("alice", "wrongpass");

        assertFalse(result.isSuccess());
        assertEquals("INVALID_CREDENTIALS", result.getError());
    }

    // EP3: Identifier not found → INVALID CREDENTIALS
    @Test
    void loginUnknownIdentifier() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("no user"));

        LoginResult result = loginService.login("ghost", "password");

        assertFalse(result.isSuccess());
        assertEquals("INVALID_CREDENTIALS", result.getError());
    }

    // EP4: Empty identifier → INVALID_INPUT (input rejected before auth)
    @Test
    void loginEmptyIdentifier() {
        LoginResult result = loginService.login("", "abc");

        assertFalse(result.isSuccess());
        assertEquals("INVALID_IDENTIFIER", result.getError());

        verifyNoInteractions(authenticationManager);
    }

    // EP5: Identifier too long (>255 chars) → INVALID_INPUT
    @Test
    void loginIdentifierTooLong() {
        String longId = "a".repeat(256);

        LoginResult result = loginService.login(longId, "abc");

        assertFalse(result.isSuccess());
        assertEquals("INVALID_IDENTIFIER", result.getError());

        verifyNoInteractions(authenticationManager);
    }

    // EP6: Identifier contains unsafe characters → INVALID_INPUT
    @Test
    void loginIdentifierUnsafeCharacters() {
        LoginResult result = loginService.login("bad\u0000char", "abc");

        assertFalse(result.isSuccess());
        assertEquals("INVALID_IDENTIFIER", result.getError());

        verifyNoInteractions(authenticationManager);
    }

    // PEP3: Empty password → INVALID_PASSWORD
    @Test
    void loginEmptyPassword() {
        LoginResult result = loginService.login("alice", "");

        assertFalse(result.isSuccess());
        assertEquals("INVALID_PASSWORD", result.getError());

        verifyNoInteractions(authenticationManager);
    }

    // PEP5: Password too long (>128 chars)
    @Test
    void loginPasswordTooLong() {
        String longPwd = "a".repeat(129);

        LoginResult result = loginService.login("alice", longPwd);

        assertFalse(result.isSuccess());
        assertEquals("INVALID_PASSWORD", result.getError());

        verifyNoInteractions(authenticationManager);
    }

    // PEP6: Password contains unsafe characters
    @Test
    void loginPasswordUnsafeCharacters() {
        LoginResult result = loginService.login("alice", "bad\u0007char");

        assertFalse(result.isSuccess());
        assertEquals("INVALID_PASSWORD", result.getError());

        verifyNoInteractions(authenticationManager);
    }

    // COMBINED: Valid input but server error → SERVER_ERROR
    @Test
    void loginServerError() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("boom"));

        LoginResult result = loginService.login("alice", "password");

        assertFalse(result.isSuccess());
        assertEquals("SERVER_ERROR", result.getError());
    }

    // BVA: Identifier length = 1 (min valid)
    @Test
    void loginIdentifierBoundaryMin() {
        Authentication authentication = auth("a", 1, "ROLE_USER");

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(jwtUtil.generateToken("a", List.of("ROLE_USER"), 1))
                .thenReturn("token");

        LoginResult result = loginService.login("a", "password");

        assertTrue(result.isSuccess());
    }

    // BVA: Identifier length = 255 (max valid)
    @Test
    void loginIdentifierBoundaryMax() {
        String id = "a".repeat(255);
        Authentication authentication = auth(id, 99, "ROLE_USER");

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(jwtUtil.generateToken(id, List.of("ROLE_USER"), 99))
                .thenReturn("token");

        LoginResult result = loginService.login(id, "password");

        assertTrue(result.isSuccess());
    }

    // BVA: Password length = 1 (min valid)
    @Test
    void loginPasswordBoundaryMin() {
        Authentication authentication = auth("alice", 42, "ROLE_USER");

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(jwtUtil.generateToken("alice", List.of("ROLE_USER"), 42))
                .thenReturn("token");

        LoginResult result = loginService.login("alice", "a");

        assertTrue(result.isSuccess());
    }

    // BVA: Password length = 128 (max valid)
    @Test
    void loginPasswordBoundaryMax() {
        String pwd = "a".repeat(128);
        Authentication authentication = auth("alice", 10, "ROLE_USER");

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(jwtUtil.generateToken("alice", List.of("ROLE_USER"), 10))
                .thenReturn("token");

        LoginResult result = loginService.login("alice", pwd);

        assertTrue(result.isSuccess());
    }
}