package com.example.lusterz.auction_house.item.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.lusterz.auction_house.bid.repository.BidRepository;
import com.example.lusterz.auction_house.exception.AuctionItemException;
import com.example.lusterz.auction_house.exception.UserException;
import com.example.lusterz.auction_house.item.dto.ItemRequest;
import com.example.lusterz.auction_house.item.model.AuctionStatus;
import com.example.lusterz.auction_house.item.model.Item;
import com.example.lusterz.auction_house.item.repository.ItemRepository;
import com.example.lusterz.auction_house.user.model.User;
import com.example.lusterz.auction_house.user.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ItemService {

    private final BidRepository bidRepository;

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemService(ItemRepository itemRepository, UserRepository userRepository, BidRepository bidRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
    }

    public Item getItem(Long id) {
        return itemRepository.findById(id)
            .orElseThrow(() -> AuctionItemException.NotFound.byId(id));
    }

    public List<Item> getAllItems(Long id) {
        return itemRepository.findAll();
    }

    public List<Item> getActiveAuctions() {
        //to-do
        return null;
    }

    public List<Item> getAllItemsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw UserException.NotFound.byId(userId);
        }
        return itemRepository.findAllByUserId(userId);    
    }
    
    @Transactional
    public Item createItem(ItemRequest auctionItemRequest) {
        User seller = userRepository.findById(auctionItemRequest.sellerId())
            .orElseThrow(() -> UserException.NotFound.byId(auctionItemRequest.sellerId()));

        if (auctionItemRequest.endTime().isAfter(LocalDateTime.now().plusWeeks(4))) {
            throw AuctionItemException.InvalidState.invalidDuration();
        }

        Item newItem = new Item();

        newItem.setTitle(auctionItemRequest.title());
        newItem.setDescription(auctionItemRequest.description());
        newItem.setItemImageUrl(auctionItemRequest.itemImageUrl());
        newItem.setStartingPrice(auctionItemRequest.startingPrice());
        newItem.setStartTime(auctionItemRequest.startTime());
        newItem.setEndTime(auctionItemRequest.endTime());
        
        newItem.setSeller(seller);

        return itemRepository.save(newItem);
    }

    @Transactional
    public Item updateItem(Long itemId, Long userId, ItemRequest auctionItemRequest) {
        Item updatedItem = itemRepository.findById(itemId)
            .orElseThrow(() -> AuctionItemException.NotFound.byId(itemId));

        if (!updatedItem.getSeller().getId().equals(userId)) {
            throw AuctionItemException.Unauthorized.notOwner();
        }

        if (!updatedItem.getBidHistory().isEmpty()) {
            throw AuctionItemException.InvalidState.hasBids();
        }

        if (updatedItem.getEndTime().isAfter(LocalDateTime.now().plusWeeks(4))) {
            throw AuctionItemException.InvalidState.invalidDuration();
        }

        updatedItem.setTitle(auctionItemRequest.title());
        updatedItem.setDescription(auctionItemRequest.description());
        updatedItem.setItemImageUrl(auctionItemRequest.itemImageUrl());
        updatedItem.setStartingPrice(auctionItemRequest.startingPrice());
        updatedItem.setStartTime(auctionItemRequest.startTime());
        updatedItem.setEndTime(auctionItemRequest.endTime());

        return itemRepository.save(updatedItem);
    }

    @Transactional
    public void deleteItem(Long itemId, Long userId) {
        Item deletedItem = itemRepository.findById(itemId)
            .orElseThrow(() -> AuctionItemException.NotFound.byId(itemId));

        if (!deletedItem.getSeller().getId().equals(userId)) {
            throw AuctionItemException.Unauthorized.notOwner();
        }

        if (!deletedItem.getBidHistory().isEmpty()) {
            throw AuctionItemException.InvalidState.hasBids();
        }

        if (!deletedItem.getStatus().equals(AuctionStatus.PENDING)) {
            throw AuctionItemException.InvalidState.alreadyStarted();
        }

        itemRepository.delete(deletedItem);
    }

    @Scheduled(cron = "0 * * * * *")
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
