package com.example.lusterz.auction_house.modules.item.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lusterz.auction_house.common.exception.ItemException;
import com.example.lusterz.auction_house.common.exception.UserException;
import com.example.lusterz.auction_house.modules.bid.repository.BidRepository;
import com.example.lusterz.auction_house.modules.item.dto.ItemDto;
import com.example.lusterz.auction_house.modules.item.dto.ItemRequest;
import com.example.lusterz.auction_house.modules.item.mapper.ItemMapper;
import com.example.lusterz.auction_house.modules.item.model.AuctionStatus;
import com.example.lusterz.auction_house.modules.item.model.Item;
import com.example.lusterz.auction_house.modules.item.repository.ItemRepository;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.repository.UserRepository;



@Service
@Transactional(readOnly = true)
public class ItemServiceImp implements ItemService{

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BidRepository bidRepository;
    private final ItemMapper itemMapper;

    public ItemServiceImp(ItemRepository itemRepository, UserRepository userRepository, BidRepository bidRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDto getItem(Long id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> ItemException.NotFound.byId(id));
        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getAllItems() {
        List<Item> list = itemRepository.findAll();
        return list.stream()
                .map(itemMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemDto> getActiveAuctions() {
        List<Item> list = itemRepository.findAllByStatus(AuctionStatus.ACTIVE);
        return list.stream()
                .map(itemMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemDto> getAllItemsBySellerId(Long sellerId) {
        if (!userRepository.existsById(sellerId)) {
            throw UserException.NotFound.byId(sellerId);
        }
        List<Item> list = itemRepository.findAllBySellerId(sellerId);   
        return list.stream()
                .map(itemMapper::toDto)
                .toList(); 
    }
    
    @Override
    @Transactional
    public ItemDto createItem(ItemRequest auctionItemRequest) {
        User seller = userRepository.findById(auctionItemRequest.sellerId())
            .orElseThrow(() -> UserException.NotFound.byId(auctionItemRequest.sellerId()));

        if (auctionItemRequest.endTime().isAfter(LocalDateTime.now().plusWeeks(4))) {
            throw ItemException.InvalidState.invalidDuration();
        }

        Item newItem = new Item();

        newItem.setTitle(auctionItemRequest.title());
        newItem.setDescription(auctionItemRequest.description());
        newItem.setItemImageUrl(auctionItemRequest.itemImageUrl());
        newItem.setStartingPrice(auctionItemRequest.startingPrice());
        newItem.setStartTime(auctionItemRequest.startTime());
        newItem.setEndTime(auctionItemRequest.endTime());
        
        newItem.setSeller(seller);

        itemRepository.save(newItem);
        return itemMapper.toDto(newItem);
    }

    @Override
    @Transactional//to-do after adding security, get user id from it not as parameter
    public ItemDto updateItem(Long itemId, Long userId, ItemRequest auctionItemRequest) {
        Item updatedItem = itemRepository.findById(itemId)
            .orElseThrow(() -> ItemException.NotFound.byId(itemId));

        if (!updatedItem.getSeller().getId().equals(userId)) {
            throw ItemException.Unauthorized.notOwner();
        }

        if (bidRepository.countByItemId(itemId) > 0) {
            throw ItemException.InvalidState.hasBids();
        }

        if (updatedItem.getEndTime().isAfter(LocalDateTime.now().plusWeeks(4))) {
            throw ItemException.InvalidState.invalidDuration();
        }

        updatedItem.setTitle(auctionItemRequest.title());
        updatedItem.setDescription(auctionItemRequest.description());
        updatedItem.setItemImageUrl(auctionItemRequest.itemImageUrl());
        updatedItem.setStartingPrice(auctionItemRequest.startingPrice());
        updatedItem.setStartTime(auctionItemRequest.startTime());
        updatedItem.setEndTime(auctionItemRequest.endTime());

        itemRepository.save(updatedItem);
        return itemMapper.toDto(updatedItem);
    }

    @Override
    @Transactional//to-do after adding security, get user id from it not as parameter
    public void deleteItem(Long itemId, Long userId) {
        Item deletedItem = itemRepository.findById(itemId)
            .orElseThrow(() -> ItemException.NotFound.byId(itemId));

        if (!deletedItem.getSeller().getId().equals(userId)) {
            throw ItemException.Unauthorized.notOwner();
        }

        if (!deletedItem.getBidHistory().isEmpty()) {
            throw ItemException.InvalidState.hasBids();
        }

        if (!deletedItem.getStatus().equals(AuctionStatus.PENDING)) {
            throw ItemException.InvalidState.alreadyStarted();
        }

        itemRepository.delete(deletedItem);
    }

    @Override
    @Transactional
    public void endAuction() {
        // update all expired items to CLOSED then fetch them
        itemRepository.closeExpiredItems(LocalDateTime.now());
        List<Item> closedItems = itemRepository.findAllByStatus(AuctionStatus.CLOSED);
        for (Item item : closedItems) {
            // check if a top bid exist and update the winner and status accordingly
            bidRepository.findTopByItemOrderByAmountDesc(item)
                .ifPresentOrElse(
                    (topBid) -> {
                        item.setWinner(topBid.getBidder());
                        item.setStatus(AuctionStatus.SOLD);
                    }, 
                    () -> {
                        item.setStatus(AuctionStatus.EXPIRED_UNSOLD);
                    }
                );    
        }
    }

    public void searchItems() {
        //to-do
    }

}
