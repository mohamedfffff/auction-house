package com.example.lusterz.auction_house.jobs.item;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.lusterz.auction_house.modules.item.service.ItemService;

import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class ProcessAuctionState {
    
    private final ItemService itemService;

    @Scheduled(cron = "0 0 * * * *")
    public void run() {
        log.info("Checking for auctions to start/end at " + LocalDateTime.now());

        itemService.startAuction();
        itemService.endAuction();
    }
}
