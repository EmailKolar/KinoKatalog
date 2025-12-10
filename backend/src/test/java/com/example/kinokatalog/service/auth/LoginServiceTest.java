package com.example.kinokatalog.service.auth;

import com.example.kinokatalog.config.JwtUtil;
import com.example.kinokatalog.config.SecurityUser;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private LoginService loginService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private Authentication auth(String username, int id) {
        SecurityUser u = mock(SecurityUser.class);
        when(u.getUsername()).thenReturn(username);
        when(u.getId()).thenReturn(id);

        Authentication a = mock(Authentication.class);
        when(a.getPrincipal()).thenReturn(u);
        when(a.getAuthorities())
                .thenReturn((Collection) List.of(new SimpleGrantedAuthority("ROLE_USER")));
        return a;
    }

    // SUCCESS CASE

    @Test
    void loginSuccess() {
        Authentication authentication = auth("alice", 42);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken("alice", List.of("ROLE_USER"), 42))
                .thenReturn("jwt");

        LoginResult result = loginService.login("alice", "pass");

        assertTrue(result.isSuccess());
        assertEquals("alice", result.getUsername());
    }

    // IDENTIFIER EP + BVA (invalid)

    static Stream<Arguments> invalidIdentifierProvider() {
        return Stream.of(
                Arguments.of("", "Empty"),
                Arguments.of("   ", "Blank"),
                Arguments.of(null, "Null"),
                Arguments.of("a".repeat(256), "Too long"),
                Arguments.of("bad\u0000x", "Unsafe char")
        );
    }

    @ParameterizedTest(name = "Invalid identifier: {1}")
    @MethodSource("invalidIdentifierProvider")
    void invalidIdentifier(String identifier, String label) {

        LoginResult r = loginService.login(identifier, "pass");

        assertFalse(r.isSuccess());
        assertEquals("INVALID_IDENTIFIER", r.getError());
        verifyNoInteractions(authenticationManager);
    }

    // PASSWORD EP + BVA (invalid)

    static Stream<Arguments> invalidPasswordProvider() {
        return Stream.of(
                Arguments.of("", "Empty"),
                Arguments.of(null, "Null"),
                Arguments.of("a".repeat(129), "Too long"),
                Arguments.of("bad\u0007x", "Unsafe char")
        );
    }

    @ParameterizedTest(name = "Invalid password: {1}")
    @MethodSource("invalidPasswordProvider")
    void invalidPassword(String password, String label) {

        LoginResult r = loginService.login("alice", password);

        assertFalse(r.isSuccess());
        assertEquals("INVALID_PASSWORD", r.getError());
        verifyNoInteractions(authenticationManager);
    }


    // Authentication failures

    @Test
    void invalidCredentials() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad"));

        LoginResult r = loginService.login("alice", "wrong");

        assertFalse(r.isSuccess());
        assertEquals("INVALID_CREDENTIALS", r.getError());
    }

    @Test
    void serverError() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("boom"));

        LoginResult r = loginService.login("alice", "password");

        assertFalse(r.isSuccess());
        assertEquals("SERVER_ERROR", r.getError());
    }


    // VALID BVA values

    @Test
    void identifierMinBoundary() {
        String id = "a";
        Authentication authentication = auth(id, 1);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken(id, List.of("ROLE_USER"), 1))
                .thenReturn("jwt");

        LoginResult r = loginService.login(id, "pw");
        assertTrue(r.isSuccess());
    }

    @Test
    void identifierMaxBoundary() {
        String id = "a".repeat(255);
        Authentication authentication = auth(id, 9);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken(id, List.of("ROLE_USER"), 9))
                .thenReturn("jwt");

        LoginResult r = loginService.login(id, "pw");
        assertTrue(r.isSuccess());
    }

    @Test
    void passwordMinBoundary() {
        Authentication authentication = auth("alice", 1);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken("alice", List.of("ROLE_USER"), 1))
                .thenReturn("jwt");

        LoginResult r = loginService.login("alice", "a");
        assertTrue(r.isSuccess());
    }

    @Test
    void passwordMaxBoundary() {
        Authentication authentication = auth("alice", 10);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken("alice", List.of("ROLE_USER"), 10))
                .thenReturn("jwt");

        LoginResult r = loginService.login("alice", "a".repeat(128));
        assertTrue(r.isSuccess());
    }
}
