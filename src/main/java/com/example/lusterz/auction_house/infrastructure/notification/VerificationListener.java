package com.example.lusterz.auction_house.infrastructure.notification;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.lusterz.auction_house.infrastructure.notification.dto.VerifyEmailEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class VerificationListener {
    
    private final EmailService emailService;

    @Async("emailExecutor")
    @EventListener
    public void handleVerifyEmail(VerifyEmailEvent event){
       emailService.sendVerificationEmail(
            event.userEmail(),
            event.username(),
            event.token()
        );
    }
}
