package com.example.kinokatalog.service;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordValidatorTest {

    private String validPasswordTemplate(int len) {
        // ensures upper + lower + digit + special
        String core = "Aa1!";
        return core + "a".repeat(len - core.length());
    }

    @Test
    void passwordLength6_invalid() {
        assertFalse(PasswordValidator.isValidPassword(validPasswordTemplate(6), "user", "mail"));
    }

    @Test
    void passwordLength7_invalid() {
        assertFalse(PasswordValidator.isValidPassword(validPasswordTemplate(7), "user", "mail"));
    }

    @Test
    void passwordLength8_valid() {
        assertTrue(PasswordValidator.isValidPassword(validPasswordTemplate(8), "user", "mail"));
    }

    @Test
    void passwordLength9_valid() {
        assertTrue(PasswordValidator.isValidPassword(validPasswordTemplate(9), "user", "mail"));
    }

    @Test
    void passwordLength127_valid() {
        assertTrue(PasswordValidator.isValidPassword(validPasswordTemplate(127), "user", "mail"));
    }

    @Test
    void passwordLength128_valid() {
        assertTrue(PasswordValidator.isValidPassword(validPasswordTemplate(128), "user", "mail"));
    }

    @Test
    void passwordLength129_invalid() {
        assertFalse(PasswordValidator.isValidPassword(validPasswordTemplate(129), "user", "mail"));
    }

    @Test
    void passwordLength130_invalid() {
        assertFalse(PasswordValidator.isValidPassword(validPasswordTemplate(130), "user", "mail"));
    }

}
