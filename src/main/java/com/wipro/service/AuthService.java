package com.wipro.service;

import com.wipro.dto.AuthResponse;
import com.wipro.dto.LoginRequest;
import com.wipro.dto.RefreshTokenRequest;
import com.wipro.dto.RegisterRequest;
import com.wipro.entity.BlacklistedToken;
import com.wipro.entity.RefreshToken;
import com.wipro.entity.Role;
import com.wipro.entity.User;
import com.wipro.repository.BlacklistedTokenRepository;
import com.wipro.repository.RefreshTokenRepository;
import com.wipro.repository.RoleRepository;
import com.wipro.repository.UserRepository;
import com.wipro.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

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

    @Value("${jwt.refresh-token-expiry}")
    private long refreshTokenExpiry;

    /**
     * Register User
     */
    public void register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("ROLE_USER not found"));

        User user = User.builder().username(request.getUsername()).email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())).enabled(true).roles(Set.of(userRole)).build();

        userRepository.save(user);
    }

    /**
     * Login
     */
    public AuthResponse login(@Valid LoginRequest request) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtService.generateAccessToken(new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), java.util.Collections.emptyList()));

        String refreshToken = createRefreshToken(user);

        return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).tokenType("Bearer").build();
    }

    /**
     * Refresh Token
     */
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken()).orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {

            refreshTokenRepository.delete(refreshToken);

            throw new RuntimeException("Refresh token expired");
        }

        User user = refreshToken.getUser();

        String newAccessToken = jwtService.generateAccessToken(new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), java.util.Collections.emptyList()));

        return AuthResponse.builder().accessToken(newAccessToken).refreshToken(refreshToken.getToken()).tokenType("Bearer").build();
    }

    /**
     * Logout
     */
    public void logout() {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        String token = authHeader.substring(7);
        Date expiration = jwtService.extractExpiration(token);

        LocalDateTime expiry = expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        BlacklistedToken blacklisted = BlacklistedToken.builder().token(token).expiryDate(expiry).build();

        blacklistedTokenRepository.save(blacklisted);
    }

    /**
     * Create Refresh Token
     */
    private String createRefreshToken(User user) {

        String token = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder().token(token).user(user).expiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiry / 1000)).build();

        refreshTokenRepository.save(refreshToken);

        return token;
    }
}