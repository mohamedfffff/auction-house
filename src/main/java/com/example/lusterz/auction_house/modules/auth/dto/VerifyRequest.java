package com.example.lusterz.auction_house.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyRequest(
    @NotBlank String token
) { }
