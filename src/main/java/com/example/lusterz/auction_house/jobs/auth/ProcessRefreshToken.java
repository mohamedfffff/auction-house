package com.example.lusterz.auction_house.jobs.auth;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.lusterz.auction_house.modules.auth.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProcessRefreshToken {

    private final RefreshTokenService refreshTokenService;
    
    @Scheduled(cron = "0 0 0 * * *")
    public void run() {
        refreshTokenService.deleteExpiredTokens();
        log.info("Deleted expired tokens");
    }
}
