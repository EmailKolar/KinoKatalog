package com.example.kinokatalog.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Writes the XSRF-TOKEN cookie so React can read it and send it back via header X-XSRF-TOKEN.
 */
public class CsrfCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        if (csrf != null) {
            // Create a readable CSRF cookie for the frontend
            ResponseCookie cookie = ResponseCookie.from("XSRF-TOKEN", csrf.getToken())
                    .httpOnly(false)  // must be readable by React (document.cookie)
                    .secure(false)    // set to true in production (HTTPS)
                    .path("/")
                    .sameSite("Lax") // Required when front + back run on different origins
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        filterChain.doFilter(request, response);
    }
}
