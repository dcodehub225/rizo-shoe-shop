package com.rizoshoeshop.rizoshoeshop.service;

import com.rizoshoeshop.rizoshoeshop.dto.user.RegisterRequest;
import com.rizoshoeshop.rizoshoeshop.dto.user.UserDTO;
import com.rizoshoeshop.rizoshoeshop.entity.User;
import com.rizoshoeshop.rizoshoeshop.exception.BadRequestException; // Import custom exception
import com.rizoshoeshop.rizoshoeshop.exception.ResourceNotFoundException; // Import custom exception
import com.rizoshoeshop.rizoshoeshop.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger; // New import
import org.slf4j.LoggerFactory;

@Service // Marks this as a Spring Service component
@Transactional // Ensures methods run within a transaction
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Injects PasswordEncoder
    private final ModelMapper modelMapper;

    @Autowired // Injects dependencies
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    // Register a new user
    public UserDTO registerUser(RegisterRequest request) {
        logger.info("Attempting to register new user with username: {}", request.getUsername());
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            logger.warn("User registration failed: Username '{}' is already taken.", request.getUsername());
            throw new BadRequestException("Username '" + request.getUsername() + "' is already taken."); // Changed to BadRequestException
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Encode password

        // Set roles. If roles are not provided or empty, default to "USER"
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            user.setRoles(Collections.singleton("USER")); // Default role
        } else {
            user.setRoles(request.getRoles());
        }

        User savedUser = userRepository.save(user);
        logger.info("User '{}' registered successfully with ID: {}", savedUser.getUsername(), savedUser.getId());
        return modelMapper.map(savedUser, UserDTO.class);
    }

    // Get all users (Admin only)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
        // No direct exceptions thrown here; Optional handles not found scenario at controller level.
    }

    // Get user by ID (Admin only)
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> modelMapper.map(user, UserDTO.class));
        // No direct exceptions thrown here; Optional handles not found scenario at controller level.
    }

    // Update user (Admin only - for roles, password reset, etc.)
    public Optional<UserDTO> updateUser(Long id, RegisterRequest request) { // Re-using RegisterRequest for update fields
        logger.info("Attempting to update user with ID: {}", id);
        return userRepository.findById(id).map(existingUser -> {
            // Ensure the new username is not duplicated by another user (excluding self)
            if (userRepository.findByUsername(request.getUsername())
                    .map(User::getId)
                    .filter(existingId -> !existingId.equals(id))
                    .isPresent()) {
                logger.warn("User update failed for ID {}: Username '{}' is already taken by another user.", id, request.getUsername());
                throw new BadRequestException("Username '" + request.getUsername() + "' is already taken."); // Changed to BadRequestException
            }

            existingUser.setUsername(request.getUsername());
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            if (request.getRoles() != null && !request.getRoles().isEmpty()) {
                existingUser.setRoles(request.getRoles());
            }

            User updatedUser = userRepository.save(existingUser);
            logger.info("User with ID {} updated successfully. Username: {}", id, existingUser.getUsername());
            return modelMapper.map(updatedUser, UserDTO.class);
        });
        // No direct exceptions thrown here for not found; Optional handles it at the controller level.
    }

    // Delete user (Admin only)
    public boolean deleteUser(Long id) {
        logger.info("Attempting to delete user with ID: {}", id);
        if (userRepository.existsById(id)) { // Check if user exists
            userRepository.deleteById(id); // Delete by ID
            logger.info("User with ID {} deleted successfully.", id);
            return true;
        }
        logger.warn("User with ID {} not found for deletion.", id);
        return false; // Return false if not found, controller handles 404.
        // Alternatively, you could throw ResourceNotFoundException here if desired:
        // else { throw new ResourceNotFoundException("User not found with ID: " + id); }
    }
}