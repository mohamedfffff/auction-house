package com.example.lusterz.auction_house.infrastructure.notification;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.lusterz.auction_house.infrastructure.dto.EndingAuctionEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class NotificationListener {
    
    private final EmailService emailService;

    @Async
    @EventListener
    public void handleEndAuction(EndingAuctionEvent event) {

        emailService.sendWinnerEmail(
            event.winnerEmail(),
            event.itemTitle(),
            event.price()
        );

        emailService.sendSellerEmail(
            event.sellerEmail(),
            event.itemTitle(),
            event.winnerUsername(),
            event.price()
        );

        emailService.sendExpiredEmail(
            event.sellerEmail(),
            event.itemTitle()
        );
    }
}
