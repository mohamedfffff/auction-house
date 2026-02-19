package com.example.lusterz.auction_house.modules.item.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ItemRequest(
    @NotBlank @Size(min = 2, max = 100) String title,
    @NotBlank String description,
    String itemImageUrl,
    @NotNull @DecimalMin(value = "0.01") BigDecimal startingPrice,
    @NotNull @FutureOrPresent OffsetDateTime startTime,
    @NotNull @Future OffsetDateTime endTime,
    @NotNull Long sellerId
) {}