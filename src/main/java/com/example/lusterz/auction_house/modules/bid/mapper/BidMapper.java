package com.example.lusterz.auction_house.modules.bid.mapper;

import org.mapstruct.Mapper;

import com.example.lusterz.auction_house.modules.bid.dto.BidDto;
import com.example.lusterz.auction_house.modules.bid.model.Bid;

@Mapper(componentModel = "spring")
public interface BidMapper {

    BidDto toDto(Bid bid);
}
