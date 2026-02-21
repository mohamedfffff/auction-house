package com.example.lusterz.auction_house.modules.user.dto;

import com.example.lusterz.auction_house.modules.user.model.UserRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRoleRequest(
    @NotNull UserRole role
) { }
