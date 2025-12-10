package com.example.kinokatalog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    private final String username = "user123";
    private final String email = "user@example.com";

    private String buildValid(int len) {
        String base = "Aa1!";
        return base + "a".repeat(len - base.length());
    }

    static Stream<Arguments> validPasswords() {
        return Stream.of(
                Arguments.of("Aa1!aaaa", "min length 8"),
                Arguments.of("Aa1!" + "a".repeat(5), "length 9"),
                Arguments.of("Aa1!" + "a".repeat(123), "length 127"),
                Arguments.of("Aa1!" + "a".repeat(124), "length 128"),
                Arguments.of("Ab9#Kd!2", "random valid")
        );
    }

    @ParameterizedTest(name = "Valid: {1}")
    @MethodSource("validPasswords")
    void validPasswords_pass(String pwd, String label) {
        assertTrue(PasswordValidator.isValidPassword(pwd, "user123", "user@example.com"));
    }


    static Stream<Arguments> invalidPasswords() {
        return Stream.of(
                Arguments.of(null, "null password"),
                Arguments.of("Aa1!", "length 4"),
                Arguments.of("Aa1!aa", "length 6"),
                Arguments.of("Aa1!aaa", "length 7"),
                Arguments.of("Aa1!" + "a".repeat(125), "length 129"),
                Arguments.of("Aa1!" + "a".repeat(126), "length 130"),
                Arguments.of("alllowercase1!", "missing uppercase"),
                Arguments.of("ALLUPPERCASE1!", "missing lowercase"),
                Arguments.of("Aa!aaaaa", "missing digit"),
                Arguments.of("Aa1aaaaa", "missing special char"),
                Arguments.of("user123", "matches username"),
                Arguments.of("user@example.com", "matches email"),
                Arguments.of("Password", "blacklisted (password)"),
                Arguments.of("123456", "blacklisted (123456)"),
                Arguments.of("Aa1!" + '\u0001', "contains control char"),
                Arguments.of("Aa1!" + '\u007F', "contains DEL char")
        );
    }

    @ParameterizedTest(name = "Invalid: {1}")
    @MethodSource("invalidPasswords")
    void invalidPasswords_fail(String pwd, String label) {
        assertFalse(PasswordValidator.isValidPassword(pwd, username, email));
    }
}
