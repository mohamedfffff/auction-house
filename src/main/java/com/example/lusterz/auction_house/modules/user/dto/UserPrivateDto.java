package com.example.lusterz.auction_house.modules.user.dto;

import java.math.BigDecimal;
import java.util.List;

import com.example.lusterz.auction_house.modules.item.model.AuctionStatus;
import com.example.lusterz.auction_house.modules.user.model.UserRole;

public record UserPrivateDto (
    Long id,
    String username,
    String email,
    String userImageUrl,
    BigDecimal balance,
    UserRole role,

    List<ItemSummaryDto> itemsForSale,
    List<BidSummaryDto> userBids
) {
    public record ItemSummaryDto (
        Long id,
        String title,
        String description,
        String itemImageUrl,
        AuctionStatus status
    ) {}

    public record BidSummaryDto (
        Long id,
        BigDecimal amount
    ) {}
}
