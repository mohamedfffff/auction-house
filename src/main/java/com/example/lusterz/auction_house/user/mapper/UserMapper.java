package com.example.lusterz.auction_house.user.mapper;

import org.mapstruct.Mapper;

import com.example.lusterz.auction_house.bid.mapper.BidMapper;
import com.example.lusterz.auction_house.item.mapper.ItemMapper;
import com.example.lusterz.auction_house.user.dto.UserPrivateDto;
import com.example.lusterz.auction_house.user.dto.UserPublicDto;
import com.example.lusterz.auction_house.user.model.User;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, BidMapper.class})
public interface UserMapper {

    UserPrivateDto toPrivateDto(User user);

    UserPublicDto toPublicDto(User user);

}
