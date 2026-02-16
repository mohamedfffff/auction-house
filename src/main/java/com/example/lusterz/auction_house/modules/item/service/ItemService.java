package com.example.lusterz.auction_house.modules.item.service;

import java.util.List;

import com.example.lusterz.auction_house.modules.item.dto.ItemDto;
import com.example.lusterz.auction_house.modules.item.dto.ItemRequest;



public interface ItemService {

    ItemDto getItem(Long id);

    List<ItemDto> getAllItems();

    List<ItemDto> getActiveAuctions();

    List<ItemDto> getAllItemsBySellerId(Long sellerId);
    
    ItemDto createItem(ItemRequest auctionItemRequest);

    ItemDto updateItem(Long itemId, Long userId, ItemRequest auctionItemRequest);

    void deleteItem(Long itemId, Long userId);

    void endAuction();

    void searchItems();

}
