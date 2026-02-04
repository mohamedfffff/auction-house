package com.example.lusterz.auction_house.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.lusterz.auction_house.Dto.AuctionItemRequest;
import com.example.lusterz.auction_house.exception.AuctionItemException;
import com.example.lusterz.auction_house.exception.UserException;
import com.example.lusterz.auction_house.model.AuctionItem;
import com.example.lusterz.auction_house.model.Bid;
import com.example.lusterz.auction_house.model.User;
import com.example.lusterz.auction_house.model.enums.AuctionStatus;
import com.example.lusterz.auction_house.repository.AuctionItemRepository;
import com.example.lusterz.auction_house.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AuctionItemService {

    private final AuctionItemRepository itemRepository;
    private final UserRepository userRepository;

    public AuctionItemService(AuctionItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public AuctionItem getItem(Long id) {
        return itemRepository.findById(id)
            .orElseThrow(() -> AuctionItemException.NotFound.byId(id));
    }

    public List<AuctionItem> getAllItems(Long id) {
        return itemRepository.findAll();
    }

    public List<Bid> getItemBids(Long itemId) {
        return itemRepository.findByIdWithBidHistory(itemId)
                .map(AuctionItem::getBidHistory)
                .orElseThrow(() -> AuctionItemException.NotFound.byId(itemId));
    }
    
    @Transactional
    public AuctionItem createItem(AuctionItemRequest auctionItemRequest) {
        User seller = userRepository.findById(auctionItemRequest.sellerId())
            .orElseThrow(() -> UserException.NotFound.byId(auctionItemRequest.sellerId()));

        if (auctionItemRequest.endTime().isAfter(LocalDateTime.now().plusWeeks(4))) {
            throw AuctionItemException.InvalidState.invalidDuration();
        }

        AuctionItem newItem = new AuctionItem();

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
    public AuctionItem updateItem(Long itemId, Long userId, AuctionItemRequest auctionItemRequest) {
        AuctionItem updatedItem = itemRepository.findById(itemId)
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
        AuctionItem deletedItem = itemRepository.findById(itemId)
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
}
