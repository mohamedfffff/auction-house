package com.example.lusterz.auction_house.item.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ItemRequest(
    String title,
    String description,
    String itemImageUrl,
    BigDecimal startingPrice,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Long sellerId
) {}