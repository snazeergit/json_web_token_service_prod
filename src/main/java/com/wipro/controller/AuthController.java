package com.wipro.controller;

import com.wipro.dto.*;
import com.wipro.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(
    name = "Authentication APIs",
    description = "User Registration, Login, Refresh Token and Logout")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  @Operation(summary = "Register a new user")
  public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {

    authService.register(request);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ApiResponse(true, "User registered successfully"));
  }

  @PostMapping("/login")
  @Operation(summary = "Authenticate user and generate JWT tokens")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/refresh")
  @Operation(summary = "Generate new access token using refresh token")
  public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {

    return ResponseEntity.ok(authService.refreshToken(request));
  }

  @PostMapping("/logout")
  @Operation(summary = "Logout user and blacklist token")
  public ResponseEntity<ApiResponse> logout() {
    authService.logout();
    return ResponseEntity.ok(new ApiResponse(true, "Logged out successfully"));
  }
}

/*
Register:
POST /api/auth/register
Request:
{
  "username":"john",
  "email":"john@gmail.com",
  "password":"password"
}
Response:
{
  "message":"User registered successfully"
}


Login:
POST /api/auth/login
Request:
{
  "username":"john",
  "password":"password"
}
Response:
{
  "accessToken":"eyJ...",
  "refreshToken":"eyJ...",
  "tokenType":"Bearer"
}


Refresh:
POST /api/auth/refresh
{
   "refreshToken":"eyJ..."
}


Logout:
POST /api/auth/logout
Authorization: Bearer eyJ...
 */
