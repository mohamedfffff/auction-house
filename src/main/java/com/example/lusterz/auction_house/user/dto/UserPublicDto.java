package com.example.lusterz.auction_house.user.dto;


public record UserPublicDto (
    String username,
    String userImageUrl,
    int totalBids,
    int totalItems
) {}
