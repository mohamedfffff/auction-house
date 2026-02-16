package com.example.lusterz.auction_house.modules.bid.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.lusterz.auction_house.modules.item.dto.ItemDto;
import com.example.lusterz.auction_house.modules.user.dto.UserPublicDto;


public record BidDto(
    BigDecimal amount,
    LocalDateTime bidTime,
    ItemDto item,
    UserPublicDto bidder
) {}
