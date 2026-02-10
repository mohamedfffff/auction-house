package com.example.lusterz.auction_house.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.lusterz.auction_house.user.dto.UserPrivateDto;
import com.example.lusterz.auction_house.user.dto.UserPublicDto;
import com.example.lusterz.auction_house.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserPrivateDto toPrivateDto(User user);

    UserPublicDto toPublicDto(User user);

}
