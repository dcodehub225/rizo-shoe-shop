package com.rizoshoeshop.rizoshoeshop.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component // Marks this as a Spring component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override // Commences an authentication scheme (e.g., sends 401 Unauthorized response)
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // This is invoked when a user tries to access a secured REST resource without supplying any credentials
        // or supplying bad credentials. We should just send a 401 Unauthorized response because there is no 'login page' to redirect to.
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}