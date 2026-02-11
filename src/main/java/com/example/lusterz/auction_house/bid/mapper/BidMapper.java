package com.example.lusterz.auction_house.bid.mapper;

import org.mapstruct.Mapper;

import com.example.lusterz.auction_house.bid.dto.BidDto;
import com.example.lusterz.auction_house.bid.model.Bid;

@Mapper(componentModel = "spring")
public interface BidMapper {

    BidDto toDto(Bid bid);
    
}
