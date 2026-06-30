package com.wipro.service;

import com.wipro.dto.AuthResponse;
import com.wipro.dto.RefreshTokenRequest;
import com.wipro.entity.RefreshToken;
import com.wipro.entity.User;
import com.wipro.exception.InvalidRefreshTokenException;
import com.wipro.exception.RefreshTokenExpiredException;
import com.wipro.exception.RefreshTokenReuseDetectedException;
import com.wipro.repository.RefreshTokenRepository;
import com.wipro.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final SecurityIncidentService securityIncidentService;

    @Value("${jwt.refresh-token-expiry}")
    private long refreshTokenExpiry;

    /**
     * createRefreshToken()
     */
    public String createRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken =
                RefreshToken.builder()
                        .token(token)
                        .user(user)
                        .expiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiry / 1000))
                        .build();
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    /**
     * Refresh Access Token
     */
    @Transactional
    public AuthResponse refreshAccessToken(RefreshTokenRequest request) {

        // Validating input refresh token
        RefreshToken existingRefreshToken =
                refreshTokenRepository
                        .findByToken(request.getRefreshToken())
                        .orElseThrow(() ->
                                new InvalidRefreshTokenException("Invalid refresh token"));

        User user = existingRefreshToken.getUser();

        // Refresh token reuse detection
        if (existingRefreshToken.isRevoked()) {

            log.info("Revoking all refresh tokens for user {}", user.getUsername());
            //updating the revoked status of all refresh tokens for the user in a new transaction
            int revoked = securityIncidentService.revokeAllSessions(user.getId());
            log.info("Revoked {} refresh tokens", revoked);

            throw new RefreshTokenReuseDetectedException(
                    "Refresh token reuse detected. All sessions revoked.");
        }

        // Deleting existing refresh token if expired
        if (existingRefreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(existingRefreshToken);
            throw new RefreshTokenExpiredException(
                    "Refresh token expired");
        }

        // Generating new access token
        String newAccessToken =
                jwtService.generateAccessToken(
                        new org.springframework.security.core.userdetails.User(
                                user.getUsername(),
                                user.getPassword(),
                                Collections.emptyList()));

        // Generate new refresh token with revoke status false and save it to the database
        String newRefreshTokenValue = createRefreshToken(user);
        RefreshToken newRefreshToken =
                RefreshToken.builder()
                        .token(newRefreshTokenValue)
                        .user(user)
                        .expiryDate(LocalDateTime.now().plusDays(7))
                        .revoked(false)
                        .build();

        refreshTokenRepository.save(newRefreshToken);

        // Revoke old refresh token instead of deleting it
        existingRefreshToken.setRevoked(true);
        refreshTokenRepository.save(existingRefreshToken);
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshTokenValue)
                .tokenType("Bearer")
                .build();
    }

    /*
    deleteByUser()
     */
    public void deleteRefreshTokenByUsername(String username) {
        refreshTokenRepository.deleteByUserUsername(username);
    }
}
