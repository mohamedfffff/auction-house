package com.example.lusterz.auction_house.jobs.auth;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.lusterz.auction_house.modules.auth.service.VerifyTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class ProcessVerifyToken {
    
    private final VerifyTokenService verifyTokenService;

    @Scheduled(cron = "0 0 0 * * *")
    public void run() {
        int count = verifyTokenService.deleteExpiredTokens();
        log.info("Deleted {} expired tokens", count);
    }
}
