package com.rizoshoeshop.rizoshoeshop.repository;

import com.rizoshoeshop.rizoshoeshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Marks this interface as a Spring Data JPA repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // To find user by username for login
}