package com.example.lusterz.auction_house.item.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lusterz.auction_house.item.dto.ItemDto;
import com.example.lusterz.auction_house.item.dto.ItemRequest;



@Service
@Transactional(readOnly = true)
public interface ItemService {

    public ItemDto getItem(Long id);

    public List<ItemDto> getAllItems();

    public List<ItemDto> getActiveAuctions();

    public List<ItemDto> getAllItemsBySellerId(Long sellerId);
    
    public ItemDto createItem(ItemRequest auctionItemRequest);

    public ItemDto updateItem(Long itemId, Long userId, ItemRequest auctionItemRequest);

    public void deleteItem(Long itemId, Long userId);

    public void endAuction();

    public void searchItems();

}
