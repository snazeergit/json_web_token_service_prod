package com.wipro.service;

import com.wipro.repository.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlacklistedTokenService {

    private final BlacklistedTokenRepository repository;

    @Transactional
    public void cleanupExpiredTokens() {
        repository.deleteByExpiryDateBefore(LocalDateTime.now());
        log.info("Expired blacklisted tokens cleaned up");
    }
}