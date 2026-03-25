package com.example.lusterz.auction_house.infrastructure.notification.dto;

public record ResetPasswordEvent(
    String email,
    String token
) { }
