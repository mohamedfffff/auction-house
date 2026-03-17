package com.example.lusterz.auction_house.common.validation;

import jakarta.validation.groups.Default;

public interface onCreate extends Default{
    // when updating auction status the @Future and @FutureOrPresent validation is triggered which throws exception
    // we can create custom validation group and assign it to those specific fields
    // validation shall be triggered only when this class is provided
    // we provide it to the controller with @Validated when creating item
    // and don't provide it when updating status
}
