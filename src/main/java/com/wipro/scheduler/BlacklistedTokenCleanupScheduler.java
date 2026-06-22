package com.wipro.scheduler;

import com.wipro.service.BlacklistedTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BlacklistedTokenCleanupScheduler {

  private final BlacklistedTokenService service;

  @Scheduled(cron = "0 */1 * * * *")
  public void cleanup() {
    service.cleanupExpiredTokens();
  }
}
