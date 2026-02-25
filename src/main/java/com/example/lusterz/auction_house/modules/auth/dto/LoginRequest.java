package com.example.lusterz.auction_house.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank String identifier,
    @NotBlank @Size(min = 8) String password
) {}
