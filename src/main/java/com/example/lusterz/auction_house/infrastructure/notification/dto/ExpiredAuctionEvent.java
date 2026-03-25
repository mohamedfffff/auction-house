package com.example.lusterz.auction_house.infrastructure.notification.dto;

public record ExpiredAuctionEvent(
    String sellerEmail,
    String sellerUsername,
    String itemTitle
) { }
