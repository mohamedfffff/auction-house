package com.example.lusterz.auction_house.modules.user.dto;

import com.example.lusterz.auction_house.modules.user.model.UserRole;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateRoleRequest(
    @NotBlank UserRole role  
) { }
