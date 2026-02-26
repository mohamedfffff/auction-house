package com.example.lusterz.auction_house.modules.auth.dto;


public record AuthResponse(
    String token,
    String type,  
    Long expiration,  
    AuthUserDto user
) { }
