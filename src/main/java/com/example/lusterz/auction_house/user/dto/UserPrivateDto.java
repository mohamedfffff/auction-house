package com.example.lusterz.auction_house.user.dto;

import java.math.BigDecimal;
import java.util.List;

import com.example.lusterz.auction_house.bid.dto.BidDto;
import com.example.lusterz.auction_house.item.dto.ItemDto;
import com.example.lusterz.auction_house.user.model.UserRole;

public record UserPrivateDto (
    Long id,
    String username,
    String email,
    String userImageUrl,
    BigDecimal balance,
    UserRole role,
    List<ItemDto> itemsForSale,
    List<BidDto> userBids
) {}
