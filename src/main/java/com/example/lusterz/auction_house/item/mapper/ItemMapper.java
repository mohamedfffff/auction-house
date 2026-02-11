package com.example.lusterz.auction_house.item.mapper;

import org.mapstruct.Mapper;

import com.example.lusterz.auction_house.item.dto.ItemDto;
import com.example.lusterz.auction_house.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    
    ItemDto toDto(Item item);
}
