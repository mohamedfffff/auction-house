package com.example.lusterz.auction_house.modules.item.mapper;

import org.mapstruct.Mapper;

import com.example.lusterz.auction_house.modules.item.dto.ItemDto;
import com.example.lusterz.auction_house.modules.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    
    ItemDto toDto(Item item);
}
