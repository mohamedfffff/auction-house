package com.example.lusterz.auction_house.jobs.item;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.lusterz.auction_house.modules.item.service.ItemService;

import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class ProcessAuctionState {
    
    private final ItemService itemService;

    @Scheduled(cron = "0 * * * * *")
    public void run() {
        int startCount = itemService.startAuction();
        log.info("Started {} auctions", startCount);
        int endCount = itemService.endAuction();
        log.info("Ended {} auctions", endCount);


    }
}
