package com.example.lusterz.auction_house.modules.user.dto;

import java.util.List;

import com.example.lusterz.auction_house.modules.item.dto.ItemDto;

public record UserPublicDto (
    String username,
    String userImageUrl,
    List<ItemDto> itemsForSale
) {}
