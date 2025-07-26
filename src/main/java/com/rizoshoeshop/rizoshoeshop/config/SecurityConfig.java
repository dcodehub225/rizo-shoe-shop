package com.rizoshoeshop.rizoshoeshop.config;

import com.rizoshoeshop.rizoshoeshop.security.JwtAuthenticationEntryPoint;
import com.rizoshoeshop.rizoshoeshop.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity // Enables Spring Security's web security support
public class SecurityConfig {

    private final UserDetailsService userDetailsService; // Our custom user details service
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint; // Handles unauthorized access
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // Filter for JWT token validation

    public SecurityConfig(UserDetailsService userDetailsService,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Bean for PasswordEncoder (BCrypt for secure password hashing)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean for DaoAuthenticationProvider (authenticates users with UserDetailsService and PasswordEncoder)
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Bean for AuthenticationManager (manages authentication process)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Configure CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow requests from your frontend's origin (e.g., React/Angular/Vue development server)
        // IMPORTANT: In production, limit this to your actual frontend domain(s)!
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:4200", "http://localhost:8080")); // Add your frontend origins here
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")); // Allowed HTTP methods
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept")); // Allowed headers
        configuration.setAllowCredentials(true); // Allow sending cookies/auth headers with requests

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply this CORS config to all paths
        return source;
    }


    // Main Security Filter Chain configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless REST APIs
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // <-- PERBAIKAN DI SINI
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)) // Handles unauthorized attempts
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Use stateless session for JWT
                .authorizeHttpRequests(authorize -> authorize
                        // Public endpoints (no authentication required)
                        .requestMatchers("/api/auth/**").permitAll() // For login and registration
                        // Specific endpoints that might be public or require roles
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll() // Allow GET for products
                        .requestMatchers(HttpMethod.GET, "/api/suppliers/**").permitAll() // Allow GET for suppliers
                        .requestMatchers(HttpMethod.GET, "/api/customers/**").permitAll() // Allow GET for customers
                        .requestMatchers(HttpMethod.GET, "/api/employees/**").permitAll() // Allow GET for employees
                        .requestMatchers(HttpMethod.GET, "/api/dashboard/**").permitAll()

                        // Authenticated endpoints (require a valid JWT token)
                        .requestMatchers(HttpMethod.POST, "/api/products/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/products/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/suppliers/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/suppliers/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/suppliers/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/customers/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/customers/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/customers/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/employees/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/employees/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/employees/**").authenticated()

                        .requestMatchers("/api/sales/**").authenticated()
                        .requestMatchers("/api/dashboard/**").hasRole("ADMIN")
                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        .anyRequest().authenticated() // All other requests must be authenticated
                );

        // Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}