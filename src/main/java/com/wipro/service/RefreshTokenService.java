package com.wipro.service;

import com.wipro.dto.AuthResponse;
import com.wipro.dto.RefreshTokenRequest;
import com.wipro.entity.RefreshToken;
import com.wipro.entity.User;
import com.wipro.exception.InvalidRefreshTokenException;
import com.wipro.exception.RefreshTokenExpiredException;
import com.wipro.repository.RefreshTokenRepository;
import com.wipro.security.JwtService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtService jwtService;

  @Value("${jwt.refresh-token-expiry}")
  private long refreshTokenExpiry;

  /** createRefreshToken() */
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

  /** Refresh Token */
  public AuthResponse refreshToken(RefreshTokenRequest request) {
    RefreshToken refreshToken =
        refreshTokenRepository
            .findByToken(request.getRefreshToken())
            .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));
    if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
      refreshTokenRepository.delete(refreshToken);
      throw new RefreshTokenExpiredException("Refresh token expired");
    }
    User user = refreshToken.getUser();
    String newAccessToken =
        jwtService.generateAccessToken(
            new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), java.util.Collections.emptyList()));
    return AuthResponse.builder()
        .accessToken(newAccessToken)
        .refreshToken(refreshToken.getToken())
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
