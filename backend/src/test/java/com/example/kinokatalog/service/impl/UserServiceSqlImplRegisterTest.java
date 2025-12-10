package com.example.kinokatalog.service.impl;

import com.example.kinokatalog.dto.RegisterRequest;
import com.example.kinokatalog.dto.UserDTO;
import com.example.kinokatalog.exception.ConflictException;
import com.example.kinokatalog.exception.InvalidDataException;
import com.example.kinokatalog.persistence.sql.entity.UserEntity;
import com.example.kinokatalog.persistence.sql.repository.UserSqlRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceSqlImplRegisterTest {

    @Mock private UserSqlRepository repo;
    @Mock private PasswordEncoder encoder;

    @InjectMocks
    private UserServiceSqlImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private RegisterRequest req(String email, String username, String password) {
        RegisterRequest r = new RegisterRequest();
        r.setEmail(email);
        r.setUsername(username);
        r.setPassword(password);
        return r;
    }

    private UserEntity mockEntity(RegisterRequest r) {
        UserEntity u = new UserEntity();
        u.setId(1);
        u.setEmail(r.getEmail());
        u.setUsername(r.getUsername());
        u.setPasswordHash("hashed");
        u.setRole("USER");
        return u;
    }

    static Stream<Arguments> invalidEmailCases() {
        return Stream.of(
                Arguments.of("bademail", "john123", "Aa1!goodPwd", "no @"),
                Arguments.of("john@com", "john123", "Aa1!goodPwd", "no dot"),
                Arguments.of("@mail.com", "john123", "Aa1!goodPwd", "empty local"),
                Arguments.of("john@", "john123", "Aa1!goodPwd", "empty domain"),
                Arguments.of("john..doe@mail.com", "john123", "Aa1!goodPwd", "double dot"),
                Arguments.of("a".repeat(65) + "@mail.com", "john123", "Aa1!goodPwd", "local > 64"),
                Arguments.of("john@" + "a".repeat(64) + ".com", "john123", "Aa1!goodPwd", "label > 63"),
                Arguments.of("a".repeat(255) + "@mail.com", "john123", "Aa1!goodPwd", "total > 254")
        );
    }

    @ParameterizedTest(name = "Invalid email → {3}")
    @MethodSource("invalidEmailCases")
    void invalidEmail_rejected(String email, String user, String pwd, String label) {
        RegisterRequest r = req(email, user, pwd);
        assertThrows(InvalidDataException.class, () -> service.register(r));
    }


    static Stream<Arguments> invalidUsernameCases() {
        return Stream.of(
                Arguments.of("test@mail.com", "a", "Aa1!goodPwd", "len=1"),
                Arguments.of("test@mail.com", "ab", "Aa1!goodPwd", "len=2"),
                Arguments.of("test@mail.com", "a".repeat(21), "Aa1!goodPwd", ">20 chars"),
                Arguments.of("test@mail.com", "a bc", "Aa1!goodPwd", "spaces"),
                Arguments.of("test@mail.com", "abc€", "Aa1!goodPwd", "illegal char"),
                Arguments.of("test@mail.com", "abc.def", "Aa1!goodPwd", "dot not allowed")
        );
    }

    @ParameterizedTest(name = "Invalid username → {3}")
    @MethodSource("invalidUsernameCases")
    void invalidUsername_rejected(String email, String username, String pwd, String label) {
        RegisterRequest r = req(email, username, pwd);
        assertThrows(InvalidDataException.class, () -> service.register(r));
    }


    static Stream<Arguments> invalidPasswordCases() {
        return Stream.of(
                Arguments.of("test@mail.com", "john123", "short7", "too short"),
                Arguments.of("test@mail.com", "john123", "Aa1!" + "a".repeat(125), "too long 129"),
                Arguments.of("test@mail.com", "john123", "alllowercase1!", "missing uppercase"),
                Arguments.of("test@mail.com", "john123", "ALLUPPERCASE1!", "missing lowercase"),
                Arguments.of("test@mail.com", "john123", "Aa!aaaaa", "missing digit"),
                Arguments.of("test@mail.com", "john123", "Aa1aaaaa", "missing special"),
                Arguments.of("test@mail.com", "john123", "john123", "matches username"),
                Arguments.of("test@mail.com", "john123", "test@mail.com", "matches email"),
                Arguments.of("test@mail.com", "john123", "Password", "blacklist"),
                Arguments.of("test@mail.com", "john123", "Aa1!" + "\u0001", "control char")
        );
    }

    @ParameterizedTest(name = "Invalid password → {3}")
    @MethodSource("invalidPasswordCases")
    void invalidPassword_rejected(String email, String username, String pwd, String label) {
        RegisterRequest r = req(email, username, pwd);
        assertThrows(InvalidDataException.class, () -> service.register(r));
    }


    static Stream<Arguments> uniquenessCases() {
        return Stream.of(
                Arguments.of(true, false, "username exists"),
                Arguments.of(false, true, "email exists")
        );
    }

    @ParameterizedTest(name = "Uniqueness conflict → {2}")
    @MethodSource("uniquenessCases")
    void uniquenessConflicts(boolean userExists, boolean emailExists, String label) {
        RegisterRequest r = req("john@mail.com", "john123", "Aa1!abcd");

        when(repo.existsByUsername("john123")).thenReturn(userExists);
        when(repo.existsByEmail("john@mail.com")).thenReturn(emailExists);

        assertThrows(ConflictException.class, () -> service.register(r));
    }


    @ParameterizedTest(name = "Valid registration → username={1}")
    @MethodSource("validRegistrationCases")
    void validRegistrations_succeed(String email, String username, String pwd) {
        RegisterRequest r = req(email, username, pwd);

        when(repo.existsByUsername(username)).thenReturn(false);
        when(repo.existsByEmail(email)).thenReturn(false);
        when(encoder.encode(pwd)).thenReturn("hashed");
        when(repo.save(any())).thenReturn(mockEntity(r));

        UserDTO dto = service.register(r);

        assertEquals(username, dto.getUsername());
        assertEquals(email, dto.getEmail());
    }

    static Stream<Arguments> validRegistrationCases() {
        return Stream.of(
                Arguments.of("john@mail.com", "john123", "Aa1!abcd"),
                Arguments.of("a".repeat(64) + "@mail.com", "abc123", "Aa1!aaaa"),
                Arguments.of("abc@" + "a".repeat(63) + ".com", "name_ok", "Aa1!" + "a".repeat(4)),
                Arguments.of("good@mail.com", "a".repeat(20), "Aa1!" + "a".repeat(120))
        );
    }
}
