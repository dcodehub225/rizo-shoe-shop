package com.rizoshoeshop.rizoshoeshop.controller;

import com.rizoshoeshop.rizoshoeshop.dto.user.LoginRequest; // Updated import
import com.rizoshoeshop.rizoshoeshop.dto.user.LoginResponse; // Updated import
import com.rizoshoeshop.rizoshoeshop.dto.user.RegisterRequest; // Updated import
import com.rizoshoeshop.rizoshoeshop.dto.user.UserDTO; // Updated import
import com.rizoshoeshop.rizoshoeshop.security.JwtTokenProvider; // Updated import
import com.rizoshoeshop.rizoshoeshop.service.UserService; // Updated import
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Marks this class as a Spring REST Controller
@RequestMapping("/api/auth") // Base URL for authentication endpoints
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Autowired // Injects dependencies
    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    // Endpoint for User Login (HTTP POST to /api/auth/login)
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(authentication);

        // Get user roles (assuming primary role for simplicity in response)
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER"); // Default role if none found

        return new ResponseEntity<>(new LoginResponse(token, loginRequest.getUsername(), role), HttpStatus.OK);
    }

    // Endpoint for User Registration (HTTP POST to /api/auth/register)
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            UserDTO registeredUser = userService.registerUser(registerRequest);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED); // Returns 201 Created status
        } catch (RuntimeException e) {
            // Catch "username already taken" or other registration errors
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
    }
}