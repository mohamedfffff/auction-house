package com.example.lusterz.auction_house.modules.user.mapper;

import org.mapstruct.Mapper;

import com.example.lusterz.auction_house.modules.bid.mapper.BidMapper;
import com.example.lusterz.auction_house.modules.item.mapper.ItemMapper;
import com.example.lusterz.auction_house.modules.user.dto.UserPrivateDto;
import com.example.lusterz.auction_house.modules.user.dto.UserPublicDto;
import com.example.lusterz.auction_house.modules.user.model.User;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, BidMapper.class})
public interface UserMapper {

    UserPrivateDto toPrivateDto(User user);

    UserPublicDto toPublicDto(User user);

}
