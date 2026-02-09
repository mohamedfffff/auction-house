package com.example.lusterz.auction_house.bid.dto;

import java.math.BigDecimal;

public record BidRequest (

    BigDecimal amount,
    Long auctionItemId

){}
