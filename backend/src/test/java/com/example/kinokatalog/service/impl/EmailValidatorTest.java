package com.example.kinokatalog.service.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmailValidatorTest {

    private final UserServiceSqlImpl service = new UserServiceSqlImpl(null, null);

    @Test
    void localPartLength0_invalid() {
        assertFalse(service.isValidEmail("@domain.com"));
    }

    @Test
    void localPartLength1_valid() {
        assertTrue(service.isValidEmail("a@domain.com"));
    }

    @Test
    void localPartLength64_valid() {
        assertTrue(service.isValidEmail("a".repeat(64) + "@domain.com"));
    }

    @Test
    void localPartLength65_invalid() {
        assertFalse(service.isValidEmail("a".repeat(65) + "@domain.com"));
    }

    @Test
    void totalLength1_invalid() {
        assertFalse(service.isValidEmail("a"));
    }

    @Test
    void totalLength254_valid() {
        String local = "a".repeat(64);
        String label1 = "b".repeat(63);
        String label2 = "c".repeat(63);
        String label3 = "d".repeat(57);
        String domain = label1 + "." + label2 + "." + label3 + ".com";

        String email = local + "@" + domain;

        assertEquals(254, email.length());
        assertTrue(service.isValidEmail(email));
    }

    @Test
    void totalLength255_invalid() {
        String local = "a".repeat(64);
        String domain = "b".repeat(186) + ".com";
        String email = local + "@" + domain;
        assertEquals(255, email.length());
        assertFalse(service.isValidEmail(email));
    }

    @Test
    void totalLength256_invalid() {
        String local = "a".repeat(64);
        String domain = "b".repeat(187) + ".com";
        String email = local + "@" + domain;
        assertEquals(256, email.length());
        assertFalse(service.isValidEmail(email));
    }

    @Test
    void domainLabel0_invalid() {
        assertFalse(service.isValidEmail("a@.com"));
    }

    @Test
    void domainLabel1_valid() {
        assertTrue(service.isValidEmail("a@b.com"));
    }

    @Test
    void domainLabel63_valid() {
        assertTrue(service.isValidEmail("a@" + "a".repeat(63) + ".com"));
    }

    @Test
    void domainLabel64_invalid() {
        assertFalse(service.isValidEmail("a@" + "a".repeat(64) + ".com"));
    }

    @Test
    void domainLabel65_invalid() {
        assertFalse(service.isValidEmail("a@" + "a".repeat(65) + ".com"));
    }

    @Test
    void missingAt_invalid() {
        assertFalse(service.isValidEmail("abcd.domain.com"));
    }

    @Test
    void domainMissingDot_invalid() {
        assertFalse(service.isValidEmail("abcd@domain"));
    }

    @Test
    void consecutiveDots_invalid() {
        assertFalse(service.isValidEmail("ab..cd@domain.com"));
    }

    @Test
    void illegalCharacters_invalid() {
        assertFalse(service.isValidEmail("ab(cd)@domain.com"));
    }
}
