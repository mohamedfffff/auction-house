package com.example.lusterz.auction_house.infrastructure.notification;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.lusterz.auction_house.infrastructure.dto.EndAuctionEvent;
import com.example.lusterz.auction_house.infrastructure.dto.ExpiredAuctionEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class NotificationListener {
    
    private final EmailService emailService;

    @Async
    @EventListener
    public void handleEndAuction(EndAuctionEvent event) {

        emailService.sendWinnerEmail(
            event.winnerEmail(),
            event.winnerUsername(),
            event.itemTitle(),
            event.price()
        );

        emailService.sendSellerEmail(
            event.sellerEmail(),
            event.sellerUsername(),
            event.itemTitle(),
            event.price(),
            event.winnerUsername()
        );

    }

    @Async
    @EventListener
    public void handleExpiredAuction(ExpiredAuctionEvent event) {

        emailService.sendExpiredEmail(
            event.sellerEmail(),
            event.sellerUsername(),
            event.itemTitle()
        );

    }
}
