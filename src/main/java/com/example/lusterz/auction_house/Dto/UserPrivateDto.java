package com.example.lusterz.auction_house.Dto;

import java.math.BigDecimal;
import java.util.List;

import com.example.lusterz.auction_house.model.AuctionItem;
import com.example.lusterz.auction_house.model.Bid;
import com.example.lusterz.auction_house.model.enums.UserRole;

public record UserPrivateDto (
    Long id,
    String username,
    String email,
    String userImageUrl,
    BigDecimal balance,
    UserRole role,
    List<AuctionItem> itemsForSale,
    List<Bid> userBids
) {}
