package com.example.lusterz.auction_house.infrastructure.dto;

public record ExpiredAuctionEvent(
    String sellerEmail,
    String sellerUsername,
    String itemTitle
) { }
