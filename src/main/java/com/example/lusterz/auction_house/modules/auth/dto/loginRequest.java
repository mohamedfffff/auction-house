package com.example.lusterz.auction_house.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank String identifier,
    @NotBlank String password
) {}
