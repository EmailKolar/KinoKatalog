package com.example.kinokatalog.service.impl;

import com.example.kinokatalog.dto.RegisterRequest;
import com.example.kinokatalog.dto.UserDTO;
import com.example.kinokatalog.exception.ConflictException;
import com.example.kinokatalog.exception.InvalidDataException;
import com.example.kinokatalog.persistence.sql.entity.UserEntity;
import com.example.kinokatalog.persistence.sql.repository.UserSqlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceSqlImplRegisterTest {

    @Mock
    private UserSqlRepository repo;

    @Mock
    private PasswordEncoder encoder;

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

    UserEntity mockSavedEntity(RegisterRequest r) {
        UserEntity u = new UserEntity();
        u.setId(1);
        u.setEmail(r.getEmail());
        u.setUsername(r.getUsername());
        u.setPasswordHash("hashed");
        u.setRole("USER");
        return u;
    }

    @Test
    void registerSuccess() {
        RegisterRequest r = req("john@mail.com", "john123", "Aa1!abcd");

        when(repo.existsByUsername("john123")).thenReturn(false);
        when(repo.existsByEmail("john@mail.com")).thenReturn(false);
        when(encoder.encode("Aa1!abcd")).thenReturn("hashed");
        when(repo.save(any())).thenReturn(mockSavedEntity(r));

        UserDTO dto = service.register(r);

        assertEquals("john123", dto.getUsername());
        assertEquals("john@mail.com", dto.getEmail());
    }

    @Test
    void invalidEmail_validUsername_validPassword() {
        RegisterRequest r = req("bademail", "john123", "Aa1!abcd");
        assertThrows(InvalidDataException.class, () -> service.register(r));
    }

    @Test
    void validEmail_invalidUsername_validPassword() {
        RegisterRequest r = req("john@mail.com", "ab", "Aa1!abcd");
        assertThrows(InvalidDataException.class, () -> service.register(r));
    }

    @Test
    void validEmail_validUsername_invalidPassword() {
        RegisterRequest r = req("john@mail.com", "john123", "weak");
        assertThrows(InvalidDataException.class, () -> service.register(r));
    }

    @Test
    void usernameAlreadyExists() {
        RegisterRequest r = req("john@mail.com", "john123", "Aa1!abcd");

        when(repo.existsByUsername("john123")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.register(r));
    }

    @Test
    void emailAlreadyExists() {
        RegisterRequest r = req("john@mail.com", "john123", "Aa1!abcd");

        when(repo.existsByUsername("john123")).thenReturn(false);
        when(repo.existsByEmail("john@mail.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.register(r));
    }

    @Test
    void combined_invalidEmail_invalidUsername_invalidPassword() {
        RegisterRequest r = req("bad", "ab", "123");
        assertThrows(InvalidDataException.class, () -> service.register(r));
    }

    @Test
    void combined_invalidEmail_validUsername_invalidPassword() {
        RegisterRequest r = req("bademail", "john123", "123");
        assertThrows(InvalidDataException.class, () -> service.register(r));
    }

    @Test
    void combined_validEmail_invalidUsername_invalidPassword() {
        RegisterRequest r = req("test@mail.com", "ab", "123");
        assertThrows(InvalidDataException.class, () -> service.register(r));
    }

    @Test
    void combined_invalidEmail_invalidUsername_validPassword() {
        RegisterRequest r = req("bademail", "ab", "Aa1!abcd");
        assertThrows(InvalidDataException.class, () -> service.register(r));
    }

    @Test
    void bva_usernameLength_1_invalid() {
        RegisterRequest r = req("test@mail.com", "a", "Aa1!abcd");
        assertThrows(InvalidDataException.class, () -> service.register(r));
    }

    @Test
    void bva_usernameLength_2_invalid() {
        RegisterRequest r = req("test@mail.com", "ab", "Aa1!abcd");
        assertThrows(InvalidDataException.class, () -> service.register(r));
    }

    @Test
    void bva_usernameLength_3_valid() {
        RegisterRequest r = req("test@mail.com", "abc", "Aa1!abcd");

        when(repo.existsByUsername("abc")).thenReturn(false);
        when(repo.existsByEmail("test@mail.com")).thenReturn(false);
        when(encoder.encode("Aa1!abcd")).thenReturn("hashed");
        when(repo.save(any())).thenReturn(mockSavedEntity(r));

        assertDoesNotThrow(() -> service.register(r));
    }

    @Test
    void bva_usernameLength_20_valid() {
        String name = "a".repeat(20);
        RegisterRequest r = req("test@mail.com", name, "Aa1!abcd");

        when(repo.existsByUsername(name)).thenReturn(false);
        when(repo.existsByEmail("test@mail.com")).thenReturn(false);
        when(encoder.encode("Aa1!abcd")).thenReturn("hashed");
        when(repo.save(any())).thenReturn(mockSavedEntity(r));

        assertDoesNotThrow(() -> service.register(r));
    }

    @Test
    void bva_usernameLength_21_invalid() {
        RegisterRequest r = req("test@mail.com", "a".repeat(21), "Aa1!abcd");
        assertThrows(InvalidDataException.class, () -> service.register(r));
    }

    @Test
    void bva_passwordLength_7_invalid() {
        RegisterRequest r = req("test@mail.com", "john123", "Aa1!ab");
        assertThrows(InvalidDataException.class, () -> service.register(r));
    }

    @Test
    void bva_passwordLength_8_valid() {
        String pwd = "Aa1!" + "a".repeat(4);
        RegisterRequest r = req("test@mail.com", "john123", pwd);

        when(repo.existsByUsername("john123")).thenReturn(false);
        when(repo.existsByEmail("test@mail.com")).thenReturn(false);
        when(encoder.encode(pwd)).thenReturn("hashed");
        when(repo.save(any())).thenReturn(mockSavedEntity(r));

        assertDoesNotThrow(() -> service.register(r));
    }

    @Test
    void bva_passwordLength_128_valid() {
        String pwd = "Aa1!" + "a".repeat(124);
        RegisterRequest r = req("test@mail.com", "john123", pwd);

        when(repo.existsByUsername("john123")).thenReturn(false);
        when(repo.existsByEmail("test@mail.com")).thenReturn(false);
        when(encoder.encode(pwd)).thenReturn("hashed");
        when(repo.save(any())).thenReturn(mockSavedEntity(r));

        assertDoesNotThrow(() -> service.register(r));
    }

    @Test
    void bva_passwordLength_129_invalid() {
        String pwd = "Aa1!" + "a".repeat(125);
        RegisterRequest r = req("test@mail.com", "john123", pwd);
        assertThrows(InvalidDataException.class, () -> service.register(r));
    }

    @Test
    void bva_emailLocalPart_64_valid() {
        String local = "a".repeat(64);
        RegisterRequest r = req(local + "@mail.com", "john123", "Aa1!abcd");

        when(repo.existsByUsername("john123")).thenReturn(false);
        when(repo.existsByEmail(r.getEmail())).thenReturn(false);
        when(encoder.encode("Aa1!abcd")).thenReturn("hashed");
        when(repo.save(any())).thenReturn(mockSavedEntity(r));

        assertDoesNotThrow(() -> service.register(r));
    }

    @Test
    void bva_emailLocalPart_65_invalid() {
        String local = "a".repeat(65);
        RegisterRequest r = req(local + "@mail.com", "john123", "Aa1!abcd");
        assertThrows(InvalidDataException.class, () -> service.register(r));
    }

    @Test
    void bva_domainLabel_63_valid() {
        String label = "a".repeat(63);
        RegisterRequest r = req("abc@" + label + ".com", "john123", "Aa1!abcd");

        when(repo.existsByUsername("john123")).thenReturn(false);
        when(repo.existsByEmail(r.getEmail())).thenReturn(false);
        when(encoder.encode("Aa1!abcd")).thenReturn("hashed");
        when(repo.save(any())).thenReturn(mockSavedEntity(r));

        assertDoesNotThrow(() -> service.register(r));
    }

    @Test
    void bva_domainLabel_64_invalid() {
        String label = "a".repeat(64);
        RegisterRequest r = req("abc@" + label + ".com", "john123", "Aa1!abcd");
        assertThrows(InvalidDataException.class, () -> service.register(r));
    }
}
