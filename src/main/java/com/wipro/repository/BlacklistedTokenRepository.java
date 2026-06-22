package com.wipro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.wipro.entity.BlacklistedToken;

import java.time.LocalDateTime;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

  boolean existsByToken(String token);

  void deleteByExpiryDateBefore(LocalDateTime now);
}
