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
                        .revoked(false)
                        .expiryDate(LocalDateTime.now().plusDays(refreshTokenExpiry))
                        .build();
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    /**
     * Refresh Access Token
     */
    /*
    Below method runs in single transaction, so if any exception occurs, the transaction will be rolled back all operations.
    refreshTokenRepository.revokeAllByUserId(user.getId()); will be rolled back if RefreshTokenReuseDetectedException occurs
    refreshTokenRepository.delete(existingRefreshToken); will be rolled back if RefreshTokenExpiredException occurs
    To prevent it we added these two exceptions to noRollbackFor attribute or we can also do
    @Transactional(propagation = Propagation.REQUIRES_NEW) on revokeAllByUserId and delete methods in RefreshTokenRepository
    however delete it built in method from CRUD Repository so we cannot add @Transactional(propagation = Propagation.REQUIRES_NEW) on it.
     */
    @Transactional(noRollbackFor = {RefreshTokenReuseDetectedException.class, RefreshTokenExpiredException.class})
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
            //updating the revoked status of all refresh tokens for the user
            int revoked= refreshTokenRepository.revokeAllByUserId(user.getId());
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
