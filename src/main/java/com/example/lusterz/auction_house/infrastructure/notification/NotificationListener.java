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

    @Async("mailExecutor")
    @EventListener
    public void handleEndAuction(EndAuctionEvent event) throws InterruptedException {

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

    @Async("mailExecutor")
    @EventListener
    public void handleExpiredAuction(ExpiredAuctionEvent event) throws InterruptedException {

        emailService.sendExpiredEmail(
            event.sellerEmail(),
            event.sellerUsername(),
            event.itemTitle()
        );

    }
}
