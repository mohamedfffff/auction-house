package com.example.lusterz.auction_house.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest (
    @NotBlank @Size(min = 2, max = 100) String username,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8) String password,
    String userImageUrl
) {}

