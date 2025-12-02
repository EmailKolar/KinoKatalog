package com.example.kinokatalog.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        log.debug("Incoming request {} {} Authorization present: {}", request.getMethod(), request.getRequestURI(), header != null);
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            log.debug("Token (first40): {}", token.length() > 40 ? token.substring(0, 40) + "..." : token);
            if (jwtUtil.validate(token)) {
                String username = jwtUtil.getUsername(token);
                log.debug("JWT valid for username: {}", username);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                var authorities = jwtUtil.getRoles(token).stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                var auth = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                Integer userId = jwtUtil.getUserId(token);
                auth.setDetails(userId);
                SecurityContextHolder.getContext().setAuthentication(auth);

                log.debug("Authentication set: principal={}, authorities={}, details(userId)={}", userDetails.getUsername(), authorities, userId);
            } else {
                log.debug("JWT validation failed for token on request {}", request.getRequestURI());
            }
        } else {
            log.debug("No Bearer Authorization header on request {}", request.getRequestURI());
        }
        filterChain.doFilter(request, response);
    }
}