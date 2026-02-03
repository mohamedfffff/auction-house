package com.example.lusterz.auction_house.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AuctionItemRequest(
    String title,
    String description,
    String itemImageUrl,
    BigDecimal startingPrice,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Long sellerId
) {}