package com.example.lusterz.auction_house.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.lusterz.auction_house.Dto.UserPrivateDto;
import com.example.lusterz.auction_house.Dto.UserPublicDto;
import com.example.lusterz.auction_house.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserPrivateDto toPrivateDto(User user);

    @Mapping(target = "totalBids", expression = "java(calcBids(user))")
    @Mapping(target = "totalItems", expression = "java(calcitems(user))")
    UserPublicDto toPublicDto(User user);

    default int calcBids(User user) {
        return user.getUserBids() != null ? user.getUserBids().size() : 0;
    }

    default int calcItems(User user) {
        return user.getItemsForSale() != null ? user.getItemsForSale().size() : 0;
    }
}
