package com.example.lusterz.auction_house.Dto;

import java.math.BigDecimal;

public record UserRequest (
    String username,
    String email,
    String password,
    String userImageUrl,
    BigDecimal balance
) {}

