package com.example.lusterz.auction_house.infrastructure.dto;

public record VerifyEmailEvent(
    String userEmail,
    String username,
    String token
) { }
