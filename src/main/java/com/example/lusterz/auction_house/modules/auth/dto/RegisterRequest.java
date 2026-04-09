package com.example.lusterz.auction_house.modules.auth.dto;

import com.example.lusterz.auction_house.modules.auth.model.AuthProviders;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Size(min = 2, max = 100) String username,
    @NotBlank @Email String email,
    @NotBlank @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,64}$",
        message = "Password must be 8–64 chars, with uppercase, lowercase, and digit"
    )
    String password,
    String userImageUrl,
    AuthProviders provider
) {}

