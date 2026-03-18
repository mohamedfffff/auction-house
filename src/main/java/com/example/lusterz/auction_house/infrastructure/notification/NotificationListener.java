package com.example.lusterz.auction_house.infrastructure.notification;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.lusterz.auction_house.infrastructure.dto.EndAuctionEvent;
import com.example.lusterz.auction_house.infrastructure.dto.ExpiredAuctionEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class NotificationListener {
    
    private final EmailService emailService;

    // custom thread that only allow single email to be processed at a time
    @Async("emailExecutor")
    // if emails fails db won't roleback as @Transactional exists on service
    // only trigger listener when db finishes
    // this helps endAuction logs to be triggerd first
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
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

    // custom thread that only allow single email to be processed at a time
    @Async("emailExecutor")
    // if emails fails db won't roleback as @Transactional exists on service
    // only trigger listener when db finishes
    // this helps endAuction logs to be triggerd first
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleExpiredAuction(ExpiredAuctionEvent event) {

        emailService.sendExpiredEmail(
            event.sellerEmail(),
            event.sellerUsername(),
            event.itemTitle()
        );

    }
}
