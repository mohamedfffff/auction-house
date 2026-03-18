package com.example.lusterz.auction_house.infrastructure.dto;

public record ResetPasswordEvent(
    String email,
    String token
) { }
