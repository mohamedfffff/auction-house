package com.example.lusterz.auction_house.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.lusterz.auction_house.Dto.AuctionItemRequest;
import com.example.lusterz.auction_house.exception.AuctionItemException;
import com.example.lusterz.auction_house.exception.UserException;
import com.example.lusterz.auction_house.model.AuctionItem;
import com.example.lusterz.auction_house.model.Bid;
import com.example.lusterz.auction_house.model.User;
import com.example.lusterz.auction_house.repository.AuctionItemRepository;
import com.example.lusterz.auction_house.repository.UserRepository;

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
    
    public AuctionItem createItem(AuctionItemRequest auctionItemRequest) {
        User seller = userRepository.findById(auctionItemRequest.getSellerId())
            .orElseThrow(() -> UserException.NotFound.byId(auctionItemRequest.getSellerId()));

        AuctionItem newItem = new AuctionItem();

        newItem.setTitle(auctionItemRequest.getTitle());
        newItem.setDescription(auctionItemRequest.getDescription());
        newItem.setItemImageUrl(auctionItemRequest.getItemImageUrl());
        newItem.setStartingPrice(auctionItemRequest.getStartingPrice());
        newItem.setStartTime(auctionItemRequest.getStartTime());
        newItem.setEndTime(auctionItemRequest.getEndTime());
        newItem.setSeller(seller);

        return itemRepository.save(newItem);
    }

    public AuctionItem updateItem(Long itemId, Long userId, AuctionItemRequest auctionItemRequest) {
        AuctionItem updatedItem = itemRepository.findById(itemId)
            .orElseThrow(() -> AuctionItemException.NotFound.byId(itemId));

        if (!updatedItem.getSeller().getId().equals(userId)) {
            throw AuctionItemException.Unauthorized.notOwner();
        }

        if (!updatedItem.getBidHistory().isEmpty()) {
            throw AuctionItemException.InvalidState.hasBids();
        }

        updatedItem.setTitle(auctionItemRequest.getTitle());
        updatedItem.setDescription(auctionItemRequest.getDescription());
        updatedItem.setItemImageUrl(auctionItemRequest.getItemImageUrl());
        updatedItem.setStartingPrice(auctionItemRequest.getStartingPrice());
        updatedItem.setStartTime(auctionItemRequest.getStartTime());
        updatedItem.setEndTime(auctionItemRequest.getEndTime());

        return itemRepository.save(updatedItem);
    }

    public void deleteItem(Long itemId, Long userId) {
        AuctionItem deletedItem = itemRepository.findById(itemId)
            .orElseThrow(() -> AuctionItemException.NotFound.byId(itemId));

        if (!deletedItem.getSeller().getId().equals(userId)) {
            throw AuctionItemException.Unauthorized.notOwner();
        }

        if (!deletedItem.getBidHistory().isEmpty()) {
            throw AuctionItemException.InvalidState.hasBids();
        }
        itemRepository.delete(deletedItem);
    }
}
