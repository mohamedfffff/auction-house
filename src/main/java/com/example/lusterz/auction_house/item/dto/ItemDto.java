package com.example.lusterz.auction_house.item.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.lusterz.auction_house.bid.dto.BidDto;
import com.example.lusterz.auction_house.item.model.AuctionStatus;
import com.example.lusterz.auction_house.user.dto.UserPublicDto;

public record ItemDto(

    String title,
    String description,
    String itemImageUrl,
    BigDecimal startingPrice,
    BigDecimal currentHighestBid,
    LocalDateTime startTime,
    LocalDateTime endTime,
    AuctionStatus status,
    UserPublicDto seller,
    UserPublicDto winner,
    List<BidDto> bidHistory

) {}
