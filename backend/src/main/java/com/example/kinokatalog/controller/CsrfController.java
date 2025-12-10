package com.example.kinokatalog.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {

    @GetMapping("/api/csrf")
    public CsrfToken csrf(CsrfToken token) {
        // This will ensure Spring resolves/generates the token and the CsrfCookieFilter will write it.
        return token;
    }
}