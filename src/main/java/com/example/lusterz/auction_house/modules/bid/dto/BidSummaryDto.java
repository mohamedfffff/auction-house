package com.example.lusterz.auction_house.modules.bid.dto;

import java.math.BigDecimal;

import com.example.lusterz.auction_house.modules.user.dto.UserSummaryDto;

public record BidSummaryDto (
    Long id,
    BigDecimal amount,
    UserSummaryDto bidder
) {}
