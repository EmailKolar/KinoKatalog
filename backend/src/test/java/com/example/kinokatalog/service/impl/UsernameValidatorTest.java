package com.example.kinokatalog.service.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsernameValidatorTest {

    private final UserServiceSqlImpl service = new UserServiceSqlImpl(null, null);

    @Test
    void length1_invalid() {
        assertFalse(service.isValidUsername("a"));
    }

    @Test
    void length2_invalid() {
        assertFalse(service.isValidUsername("ab"));
    }

    @Test
    void length3_valid() {
        assertTrue(service.isValidUsername("abc"));
    }

    @Test
    void length4_valid() {
        assertTrue(service.isValidUsername("abcd"));
    }

    @Test
    void length19_valid() {
        assertTrue(service.isValidUsername("a".repeat(19)));
    }

    @Test
    void length20_valid() {
        assertTrue(service.isValidUsername("a".repeat(20)));
    }

    @Test
    void length21_invalid() {
        assertFalse(service.isValidUsername("a".repeat(21)));
    }

    @Test
    void length22_invalid() {
        assertFalse(service.isValidUsername("a".repeat(22)));
    }

    @Test
    void illegalCharacters_invalid() {
        assertFalse(service.isValidUsername("abcd efg"));
    }

    @Test
    void leadingSpace_invalid() {
        assertFalse(service.isValidUsername(" abc"));
    }

    @Test
    void trailingSpace_invalid() {
        assertFalse(service.isValidUsername("abc "));
    }

    @Test
    void validSpecialCharacters() {
        assertTrue(service.isValidUsername("user_name-123"));
    }
}
