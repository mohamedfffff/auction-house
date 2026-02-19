package com.example.lusterz.auction_house.modules.user.dto;

import java.util.List;

import com.example.lusterz.auction_house.modules.item.dto.ItemSummaryDto;

public record UserPublicDto (
    String username,
    String userImageUrl,
    List<ItemSummaryDto> itemsForSale
) { }
