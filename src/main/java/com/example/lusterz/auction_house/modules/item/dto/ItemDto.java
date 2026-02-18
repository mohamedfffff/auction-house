package com.example.lusterz.auction_house.modules.item.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.lusterz.auction_house.modules.bid.dto.BidSummaryDto;
import com.example.lusterz.auction_house.modules.item.model.AuctionStatus;
import com.example.lusterz.auction_house.modules.user.dto.UserSummaryDto;

public record ItemDto(

    Long id,
    String title,
    String description,
    String itemImageUrl,
    BigDecimal startingPrice,
    BigDecimal currentHighestBid,
    LocalDateTime startTime,
    LocalDateTime endTime,
    AuctionStatus status,
    UserSummaryDto seller,
    UserSummaryDto winner,
    List<BidSummaryDto> bidHistory

) { }
