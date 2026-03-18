package com.example.lusterz.auction_house.jobs.auth;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.lusterz.auction_house.common.exception.AuthException.VerifyToken;
import com.example.lusterz.auction_house.modules.auth.service.RefreshTokenService;
import com.example.lusterz.auction_house.modules.auth.service.ResetPasswordTokenService;
import com.example.lusterz.auction_house.modules.auth.service.VerifyTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProcessTokens {

    private final RefreshTokenService refreshTokenService;
    private final VerifyTokenService verifyTokenService;
    private final ResetPasswordTokenService resetPasswordTokenService;
    
    @Scheduled(cron = "0 0 0 * * *")
    public void run() {

        int countRefresh = refreshTokenService.deleteExpiredTokens();
        log.info("Deleted {} expired refresh tokens", countRefresh);

        int countVerify = verifyTokenService.deleteExpiredTokens();
        log.info("Deleted {} expired verify tokens", countVerify);

        int countReset = resetPasswordTokenService.deleteExpiredTokens();
        log.info("Deleted {} expired reset password tokens", countReset);
        
    }
}
