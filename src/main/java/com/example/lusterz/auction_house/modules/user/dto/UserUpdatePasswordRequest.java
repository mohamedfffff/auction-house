package com.example.lusterz.auction_house.modules.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdatePasswordRequest(
    
    @NotBlank String oldPassword,
    @NotBlank @Size(min = 8) String newPassword,
    @NotBlank String confirmPassword

) {
}
