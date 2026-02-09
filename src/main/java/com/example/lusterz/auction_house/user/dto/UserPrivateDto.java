package com.example.lusterz.auction_house.user.dto;

import java.math.BigDecimal;
import java.util.List;

import com.example.lusterz.auction_house.bid.model.Bid;
import com.example.lusterz.auction_house.item.model.Item;
import com.example.lusterz.auction_house.user.model.UserRole;

public record UserPrivateDto (
    Long id,
    String username,
    String email,
    String userImageUrl,
    BigDecimal balance,
    UserRole role,
    List<Item> itemsForSale,
    List<Bid> userBids
) {}
