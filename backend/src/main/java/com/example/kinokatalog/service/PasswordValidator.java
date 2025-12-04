package com.example.kinokatalog.service;


import java.util.Set;

public class PasswordValidator {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;

    // special chars: adjust as needed
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:'\",.<>?/~`";

    public static boolean isValidPassword(String password, String username, String email) {

        if (password == null) return false;

        // 1. Length checks
        if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH)
            return false;

        String lowerPwd = password.toLowerCase();

        // 2. Cannot equal username or email (case-insensitive)
        if (username != null && lowerPwd.equals(username.toLowerCase()))
            return false;

        if (email != null && lowerPwd.equals(email.toLowerCase()))
            return false;

        // 3. Check if password is in blacklist
        if (CommonPasswords.BLACKLIST.contains(lowerPwd))
            return false;

        // 4. Check category strength (must match all categories)
        int categories = 0;

        if (containsLowercase(password)) categories++;
        if (containsUppercase(password)) categories++;
        if (containsDigit(password)) categories++;
        if (containsSpecial(password)) categories++;

        return categories >= 4;
    }

    private static boolean containsLowercase(String s) {
        return s.chars().anyMatch(Character::isLowerCase);
    }

    private static boolean containsUppercase(String s) {
        return s.chars().anyMatch(Character::isUpperCase);
    }

    private static boolean containsDigit(String s) {
        return s.chars().anyMatch(Character::isDigit);
    }

    private static boolean containsSpecial(String s) {
        return s.chars().anyMatch(c -> SPECIAL_CHARS.indexOf(c) >= 0);
    }


    public static class CommonPasswords {

        // Add more or load from file if you want
        public static final Set<String> BLACKLIST = Set.of(
                "password",
                "123456",
                "123456789",
                "qwerty",
                "letmein",
                "admin",
                "welcome",
                "iloveyou",
                "monkey",
                "abc123"
        );
    }
}

