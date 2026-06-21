package com.wipro.security;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, java.io.IOException {

        String errorCode = (String) request.getAttribute("jwt_error");

        if (errorCode == null) {
            errorCode = "AUTHENTICATION_FAILED";
        }

        String message = switch (errorCode) {
            case "ACCESS_TOKEN_EXPIRED" -> "Access token expired";
            case "INVALID_ACCESS_TOKEN" -> "Invalid access token";
            default -> "Authentication required";
        };

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        response.getWriter().write("""
        {
          "timestamp": "%s",
          "status": 401,
          "error": "Unauthorized",
          "errorCode": "%s",
          "message": "%s",
          "path": "%s"
        }
        """.formatted(
                java.time.LocalDateTime.now(),
                errorCode,
                message,
                request.getRequestURI()
        ));
    }
}