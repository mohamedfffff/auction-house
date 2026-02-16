package com.example.lusterz.auction_house.modules.user.dto;

import java.util.List;

import com.example.lusterz.auction_house.modules.item.model.AuctionStatus;

public record UserPublicDto (
    String username,
    String userImageUrl,
    List<ItemSummaryDto> itemsForSale
) {
    public record ItemSummaryDto (
        Long id,
        String title,
        String description,
        String itemImageUrl,
        AuctionStatus status
    ) {}
}
