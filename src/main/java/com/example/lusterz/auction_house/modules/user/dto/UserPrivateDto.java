package com.example.lusterz.auction_house.modules.user.dto;

import java.math.BigDecimal;
import java.util.List;

import com.example.lusterz.auction_house.modules.bid.dto.BidSummaryDto;
import com.example.lusterz.auction_house.modules.item.dto.ItemSummaryDto;
import com.example.lusterz.auction_house.modules.user.model.UserRole;

public record UserPrivateDto (
    Long id,
    String username,
    String email,
    String userImageUrl,
    BigDecimal balance,
    UserRole role,

    List<ItemSummaryDto> itemsForSale,
    List<BidSummaryDto> userBids
) { }
