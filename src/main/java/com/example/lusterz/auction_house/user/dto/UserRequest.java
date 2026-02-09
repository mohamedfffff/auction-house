package com.example.lusterz.auction_house.user.dto;

import java.math.BigDecimal;

public record UserRequest (
    String username,
    String email,
    String password,
    String userImageUrl,
    BigDecimal balance
) {}

