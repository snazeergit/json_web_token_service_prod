package com.wipro.exception;

import com.wipro.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UsernameAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleUsernameAlreadyExistsException(
      UsernameAlreadyExistsException ex) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            400,
            "Bad Request",
            "USERNAME_ALREADY_EXISTS",
            ex.getMessage(),
            "/api/auth/register");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(
      EmailAlreadyExistsException ex) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            400,
            "Bad Request",
            "EMAIL_ALREADY_EXISTS",
            ex.getMessage(),
            "/api/auth/register");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(UserRoleNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserRoleNotFoundException(
      UserRoleNotFoundException ex) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            400,
            "Bad Request",
            "USER_ROLE_NOT_FOUND",
            ex.getMessage(),
            "/api/auth/register");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            404,
            "Not Found",
            "USER_NOT_FOUND",
            ex.getMessage(),
            "/api/auth/login");
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(RefreshTokenExpiredException.class)
  public ResponseEntity<ErrorResponse> handleRefreshTokenExpiredException(
      RefreshTokenExpiredException ex) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            400,
            "Bad Request",
            "REFRESH_TOKEN_EXPIRED",
            ex.getMessage(),
            "/api/auth/refresh");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(InvalidRefreshTokenException.class)
  public ResponseEntity<ErrorResponse> handleInvalidTokenException(
      InvalidRefreshTokenException ex) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            401,
            "Unauthorized",
            "INVALID_REFRESH_TOKEN",
            ex.getMessage(),
            "/api/auth/refresh");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleInvalidCredentials(
      InvalidCredentialsException ex, HttpServletRequest request) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            401,
            "Unauthorized",
            "INVALID_CREDENTIALS",
            ex.getMessage(),
            request.getRequestURI());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleAuthenticationCredentialsNotFoundException(
      AuthenticationCredentialsNotFoundException ex, HttpServletRequest request) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            401,
            "Unauthorized",
            "AUTHENTICATION_REQUIRED",
            ex.getMessage(),
            request.getRequestURI());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }
}
