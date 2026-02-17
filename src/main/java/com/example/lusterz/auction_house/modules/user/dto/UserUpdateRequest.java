package com.example.lusterz.auction_house.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest (
    @NotBlank @Size(min = 2, max = 100) String username,
    @NotBlank @Email String email,
    String userImageUrl
) {}

