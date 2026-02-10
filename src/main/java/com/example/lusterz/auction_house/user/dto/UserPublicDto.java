package com.example.lusterz.auction_house.user.dto;

import java.util.List;

import com.example.lusterz.auction_house.item.model.Item;

public record UserPublicDto (
    String username,
    String userImageUrl,
    //change to dto when done
    List<Item> itemsForSale
) {}
