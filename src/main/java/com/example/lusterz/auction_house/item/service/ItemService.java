package com.example.lusterz.auction_house.item.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lusterz.auction_house.item.dto.ItemDto;
import com.example.lusterz.auction_house.item.dto.ItemRequest;



@Service
@Transactional(readOnly = true)
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
