package com.example.lusterz.auction_house.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateEmailRequest (
    @NotBlank @Email String email
) { }

