package com.example.lusterz.auction_house.jobs.item;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.lusterz.auction_house.modules.item.service.ItemService;

@Component
public class AuctionClosingJob {
    
    private final ItemService itemService;

    public AuctionClosingJob(ItemService itemService) {
        this.itemService = itemService;
    }

    //change to 0 * * * * * on production or testing
    @Scheduled(cron = "0 0 0 * * *")
    public void run() {
        itemService.endAuction();
    }
}
