package com.rizoshoeshop.rizoshoeshop.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Marks this as a Spring component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider; // Injects the JWT token provider
    private final UserDetailsService userDetailsService; // Injects our custom user details service

    @Autowired
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override // Filters each incoming request
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. Get JWT token from HTTP request
        String token = getTokenFromRequest(request);

        // 2. Validate token
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            // Get username from token
            String username = jwtTokenProvider.getUsername(token);

            // Load user associated with token
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Create authentication object
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null, // Credentials are null for JWT as it's stateless
                    userDetails.getAuthorities() // User's roles/authorities
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set authentication to Spring Security Context
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    // Helper method to extract JWT token from request header
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization"); // Get Authorization header

        // Check if header starts with "Bearer " and extract token
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length()); // Return the token part
        }
        return null;
    }
}