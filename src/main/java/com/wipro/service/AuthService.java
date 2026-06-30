package com.wipro.service;

import com.wipro.dto.AuthResponse;
import com.wipro.dto.LoginRequest;
import com.wipro.dto.RegisterRequest;
import com.wipro.entity.BlacklistedToken;
import com.wipro.entity.Role;
import com.wipro.entity.User;
import com.wipro.exception.*;
import com.wipro.exception.AuthenticationCredentialsNotFoundException;
import com.wipro.repository.BlacklistedTokenRepository;
import com.wipro.repository.RefreshTokenRepository;
import com.wipro.repository.RoleRepository;
import com.wipro.repository.UserRepository;
import com.wipro.security.CustomUserDetails;
import com.wipro.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final HttpServletRequest request;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.refresh-token-expiry}")
    private long refreshTokenExpiry;

    /**
     * Register User
     */
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        Role userRole =
                roleRepository
                        .findByName("ROLE_USER")
                        .orElseThrow(() -> new UserRoleNotFoundException("ROLE_USER not found"));
        User user =
                User.builder()
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .enabled(true)
                        .roles(Set.of(userRole))
                        .build();
        userRepository.save(user);
    }

    /**
     * Login
     */
    public AuthResponse login(@Valid LoginRequest request) {
        Authentication authentication;
        try {
            authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getUsername(), request.getPassword()));
        } catch (DisabledException ex) {
            throw new DisabledException("User is disabled");
        } catch (LockedException ex) {
            throw new LockedException("User has been locked");
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = refreshTokenService.createRefreshToken(user);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    /**
     * Logout
     */
    public void logout() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
            throw new AuthenticationCredentialsNotFoundException(
                    "Authorization token is missing or invalid");
        }
        String accessToken = authHeader.substring(7).trim();
        if (accessToken.isEmpty()) {
            throw new AuthenticationCredentialsNotFoundException(
                    "Authorization token is missing or invalid");
        }

        try {
            String username = jwtService.extractUsername(accessToken);

            // Delete refresh token
            refreshTokenService.deleteRefreshTokenByUsername(username);

            // Check if access token is already blacklisted
            blacklistToken(accessToken);

        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            String username = ex.getClaims().getSubject();
            refreshTokenService.deleteRefreshTokenByUsername(username);
            // no need to blacklist, as access token already expired
        }
    }

    /*
    Checks if accessToken is blacklisted, if yes then throws TokenAlreadyRevokedException, else it will blacklist the accessToken.
     */
    private void blacklistToken(String token) {
        if (blacklistedTokenRepository.existsByToken(token)) {
            //For idempotent logout behavior just add a return statement and comment throw statement.
            throw new TokenAlreadyRevokedException("Token already blacklisted");
        }
        LocalDateTime expiry =
                jwtService
                        .extractExpiration(token)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
        blacklistedTokenRepository.save(
                BlacklistedToken.builder().token(token).expiryDate(expiry).build());
    }

}
