package com.rizoshoeshop.rizoshoeshop.controller;

import com.rizoshoeshop.rizoshoeshop.dto.user.RegisterRequest; // Re-using for update
import com.rizoshoeshop.rizoshoeshop.dto.user.UserDTO;
import com.rizoshoeshop.rizoshoeshop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // For role-based access control
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marks this class as a Spring REST Controller
@RequestMapping("/api/users") // Base URL for user management endpoints
public class UserController {

    private final UserService userService;

    @Autowired // Injects the UserService
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint to get all users (Admin only)
    // HTTP GET to /api/users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Only users with ADMIN role can access this
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Endpoint to get user by ID (Admin only)
    // HTTP GET to /api/users/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only users with ADMIN role can access this
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(userDTO -> new ResponseEntity<>(userDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // If not found, return 404 Not Found
    }

    // Endpoint to update a user (Admin only)
    // HTTP PUT to /api/users/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only users with ADMIN role can access this
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody RegisterRequest request) {
        // Removed try-catch block, GlobalExceptionHandler will handle BadRequestException or ResourceNotFoundException
        return userService.updateUser(id, request)
                .map(userDTO -> new ResponseEntity<>(userDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // If not found, return 404 Not Found
    }

    // Endpoint to delete a user (Admin only)
    // HTTP DELETE to /api/users/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only users with ADMIN role can access this
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Return 404 Not Found if user not found
    }
}