package com.example.lusterz.auction_house.modules.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateUsernameRequest (
    @NotBlank @Size(min = 2, max = 100) String username
) { }

