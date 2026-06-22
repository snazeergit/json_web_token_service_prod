package com.wipro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.wipro.entity.BlacklistedToken;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

  boolean existsByToken(String token);
}
