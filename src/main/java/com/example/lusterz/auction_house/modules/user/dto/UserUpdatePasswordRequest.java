package com.example.lusterz.auction_house.modules.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserUpdatePasswordRequest(
    @NotBlank String oldPassword,
    @NotBlank @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,64}$",
        message = "Password must be 8–64 chars, with uppercase, lowercase, and digit"
    )
    String newPassword
) { }
