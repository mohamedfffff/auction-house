package com.example.lusterz.auction_house.infrastructure.notification;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.lusterz.auction_house.infrastructure.notification.dto.ResetPasswordEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ResetPasswordListener {
    
    private final EmailService emailService;

    @Async("emailExecutor")
    @EventListener
    public void handleResetPasswordEmail(ResetPasswordEvent event){
        emailService.sendResetPasswordEmail(
            event.email(),
            event.token()
        );
    }
}
