package com.example.lusterz.auction_house.Dto;


public record UserPublicDto (
    String username,
    String userImageUrl,
    int totalBids,
    int totalItems
) {}
