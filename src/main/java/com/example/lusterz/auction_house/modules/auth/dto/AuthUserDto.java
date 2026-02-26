package com.example.lusterz.auction_house.modules.auth.dto;

import com.example.lusterz.auction_house.modules.user.model.UserRole;

public record AuthUserDto (
    String username,
    UserRole role
){ }
