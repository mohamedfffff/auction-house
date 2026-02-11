package com.example.lusterz.auction_house.bid.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.lusterz.auction_house.item.dto.ItemDto;
import com.example.lusterz.auction_house.user.dto.UserPublicDto;


public record BidDto(
    BigDecimal amount,
    LocalDateTime bidTime,
    ItemDto auctionItem,
    UserPublicDto bidder
) {}
