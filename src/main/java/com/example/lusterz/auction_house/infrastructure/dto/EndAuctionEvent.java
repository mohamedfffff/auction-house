package com.example.lusterz.auction_house.infrastructure.dto;

import java.math.BigDecimal;

public record EndAuctionEvent(

    String winnerEmail,    
    String winnerUsername,
    String sellerEmail,
    String sellerUsername,
    String itemTitle,
    BigDecimal price
    
) { }
