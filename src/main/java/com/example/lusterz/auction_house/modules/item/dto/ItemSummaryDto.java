package com.example.lusterz.auction_house.modules.item.dto;

import com.example.lusterz.auction_house.modules.item.model.AuctionStatus;

public record ItemSummaryDto (
    Long id,
    String title,
    String description,
    String itemImageUrl,
    AuctionStatus status
) {}
