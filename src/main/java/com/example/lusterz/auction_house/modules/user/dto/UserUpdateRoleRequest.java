package com.example.lusterz.auction_house.modules.user.dto;

import com.example.lusterz.auction_house.modules.user.model.UserRole;

public record UserUpdateRoleRequest(
    UserRole role  
) { }
