package com.example.lusterz.auction_house.modules.auth.dto;


public record AuthResponse(
    String accessToken,
    String refreshToken,
    String type,  
    Long expiration,  
    AuthUserDto user
) { }
