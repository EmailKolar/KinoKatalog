package com.example.kinokatalog.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UsernameValidatorTest {

    private UserServiceSqlImpl service;

    @BeforeEach
    void setup() {
        service = new UserServiceSqlImpl(null, null);
    }

    static Stream<String> validUsernames() {
        return Stream.of(
                "abc",
                "abcd",
                "a".repeat(19),
                "a".repeat(20),
                "user_name-123",
                "A_B-C9"
        );
    }

    @ParameterizedTest
    @MethodSource("validUsernames")
    void validUsernames_pass(String username) {
        assertTrue(service.isValidUsername(username));
    }

    static Stream<Arguments> invalidUsernames() {
        return Stream.of(
                Arguments.of("a", "length1"),
                Arguments.of("ab", "length2"),
                Arguments.of("a".repeat(21), "length21"),
                Arguments.of("a".repeat(22), "length22"),
                Arguments.of("abcd efg", "contains space"),
                Arguments.of(" abc", "leading space"),
                Arguments.of("abc ", "trailing space"),
                Arguments.of("ab$cd", "illegal char $"),
                Arguments.of("abc.def", "dot not allowed"),
                Arguments.of(".", "dot only"),
                Arguments.of(null, "null")
        );
    }

    @ParameterizedTest(name = "Invalid: {1}")
    @MethodSource("invalidUsernames")
    void invalidUsernames_fail(String username, String label) {
        assertFalse(service.isValidUsername(username));
    }
}
