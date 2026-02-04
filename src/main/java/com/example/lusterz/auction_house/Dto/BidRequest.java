package com.example.lusterz.auction_house.Dto;

import java.math.BigDecimal;

public record BidRequest (

    BigDecimal amount,
    Long auctionItemId

){}
