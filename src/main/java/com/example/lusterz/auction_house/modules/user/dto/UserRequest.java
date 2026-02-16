package com.example.lusterz.auction_house.modules.user.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UserRequest (
    @NotBlank @Size(min = 2, max = 100) String username,
    @NotBlank @Email String email,
    @NotBlank String password,
    String userImageUrl,
    @PositiveOrZero BigDecimal balance
) {}

