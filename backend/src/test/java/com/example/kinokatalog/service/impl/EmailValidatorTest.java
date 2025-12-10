package com.example.kinokatalog.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EmailValidatorTest {

    private UserServiceSqlImpl service;

    @BeforeEach
    void setup() {
        service = new UserServiceSqlImpl(null, null); // passwordEncoder & repo unused
    }

    // Local Part Length BVA
    static Stream<Arguments> localPartInvalid() {
        return Stream.of(
                Arguments.of("@domain.com", "local=0"),
                Arguments.of("a".repeat(65) + "@domain.com", "local>64")
        );
    }

    @ParameterizedTest(name = "Invalid local part: {1}")
    @MethodSource("localPartInvalid")
    void localPartInvalidCases(String email, String label) {
        assertFalse(service.isValidEmail(email));
    }

    static Stream<String> localPartValid() {
        return Stream.of(
                "a@domain.com",
                "a".repeat(64) + "@domain.com"
        );
    }

    @ParameterizedTest
    @MethodSource("localPartValid")
    void localPartValidCases(String email) {
        assertTrue(service.isValidEmail(email));
    }

    // Total Length BVA
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

    static Stream<Arguments> totalLengthInvalid() {
        return Stream.of(
                Arguments.of(255, 186, "len=255"),
                Arguments.of(256, 187, "len=256")
        );
    }

    @ParameterizedTest(name = "Invalid total length: {2}")
    @MethodSource("totalLengthInvalid")
    void totalLengthInvalidCases(int totalLen, int domainChars, String label) {

        String local = "a".repeat(64);
        String domain = "b".repeat(domainChars) + ".com";
        String email = local + "@" + domain;

        assertEquals(totalLen, email.length());
        assertFalse(service.isValidEmail(email));
    }

    // Domain Label BVA
    static Stream<Arguments> domainLabelInvalid() {
        return Stream.of(
                Arguments.of("a@.com", "label=0"),
                Arguments.of("a@" + "a".repeat(64) + ".com", "label=64"),
                Arguments.of("a@" + "a".repeat(65) + ".com", "label=65")
        );
    }

    @ParameterizedTest(name = "Invalid domain label: {1}")
    @MethodSource("domainLabelInvalid")
    void domainLabelInvalidCases(String email, String label) {
        assertFalse(service.isValidEmail(email));
    }

    static Stream<String> domainLabelValid() {
        return Stream.of(
                "a@b.com",
                "a@" + "a".repeat(63) + ".com"
        );
    }

    @ParameterizedTest
    @MethodSource("domainLabelValid")
    void domainLabelValidCases(String email) {
        assertTrue(service.isValidEmail(email));
    }

    // Format invalid cases (EP)
    static Stream<String> invalidFormat() {
        return Stream.of(
                "abcd.domain.com",      // missing @
                "abcd@domain",          // missing dot
                "ab..cd@domain.com",    // consecutive dots
                "ab(cd)@domain.com"     // illegal character
        );
    }
    @ParameterizedTest
    @MethodSource("invalidFormat")
    void invalidFormatCases(String email) {
        assertFalse(service.isValidEmail(email));
    }

    // Success sanity test (multi-label)
    @Test
    void validComplexEmail_success() {
        assertTrue(service.isValidEmail("alice.smith+test@sub.example.co.uk"));
    }
}
