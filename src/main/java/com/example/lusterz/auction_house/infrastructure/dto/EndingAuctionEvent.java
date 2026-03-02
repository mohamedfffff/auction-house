package com.example.lusterz.auction_house.infrastructure.dto;

import java.math.BigDecimal;

public record EndingAuctionEvent(
    Long itemId,
    String itemTitle,
    String winnerEmail,    
    String winnerUsername,
    String sellerEmail,
    BigDecimal price
) { }
