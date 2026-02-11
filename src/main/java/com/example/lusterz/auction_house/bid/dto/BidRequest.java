package com.example.lusterz.auction_house.bid.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public record BidRequest (

    @NotBlank @DecimalMin(value = "0.01") BigDecimal amount,
    @NotBlank Long auctionItemId

){}
