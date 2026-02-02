package com.example.lusterz.auction_house.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuctionItemRequest {

    private String title;
    private String description;
    private String itemImageUrl;
    private BigDecimal startingPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long sellerId;
}
