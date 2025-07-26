package com.rizoshoeshop.rizoshoeshop.security;

import io.jsonwebtoken.*; // Import all from io.jsonwebtoken
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value; // For injecting properties from application.properties
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component // Marks this as a Spring component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}") // JWT secret key from application.properties
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}") // JWT expiration time from application.properties
    private long jwtExpirationDate;

    // Generate JWT token
    public String generateToken(Authentication authentication) {
        String username = authentication.getName(); // Get username from authentication object
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        String token = Jwts.builder()
                .setSubject(username) // Subject of the token (username)
                .setIssuedAt(new Date()) // Token issue date
                .setExpiration(expireDate) // Token expiration date
                .signWith(key()) // Sign the token with the secret key
                .compact(); // Compact into a URL-safe string
        return token;
    }

    // Get username from JWT token
    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key()) // Set the signing key
                .build()
                .parseClaimsJws(token) // Parse the token
                .getBody(); // Get the claims body
        return claims.getSubject(); // Return the username (subject)
    }

    // Validate JWT token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parse(token); // Parse and validate the token
            return true;
        } catch (MalformedJwtException e) {
            // Log this exception
            System.out.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            // Log this exception
            System.out.println("Expired JWT token: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            // Log this exception
            System.out.println("Unsupported JWT token: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Log this exception
            System.out.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }

    // Helper method to get the signing key
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)); // Decode Base64 secret
    }
}