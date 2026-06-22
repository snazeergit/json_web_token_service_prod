package com.wipro.repository;

import com.wipro.entity.RefreshToken;
import com.wipro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Optional<RefreshToken> findByToken(String token);

  void deleteByUserUsername(String user);
}
