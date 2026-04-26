package com.example.lusterz.auction_house.modules.item.service;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lusterz.auction_house.common.exception.ItemException;
import com.example.lusterz.auction_house.common.exception.UserException;
import com.example.lusterz.auction_house.infrastructure.notification.dto.EndAuctionEvent;
import com.example.lusterz.auction_house.infrastructure.notification.dto.ExpiredAuctionEvent;
import com.example.lusterz.auction_house.modules.bid.repository.BidRepository;
import com.example.lusterz.auction_house.modules.item.dto.ItemDto;
import com.example.lusterz.auction_house.modules.item.dto.ItemRequest;
import com.example.lusterz.auction_house.modules.item.dto.ItemUpdateRequest;
import com.example.lusterz.auction_house.modules.item.mapper.ItemMapper;
import com.example.lusterz.auction_house.modules.item.model.AuctionStatus;
import com.example.lusterz.auction_house.modules.item.model.Item;
import com.example.lusterz.auction_house.modules.item.repository.ItemRepository;
import com.example.lusterz.auction_house.modules.bid.model.Bid;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BidRepository bidRepository;
    private final ItemMapper itemMapper;
    private final ApplicationEventPublisher eventPublisher;

    public ItemDto getItem(Long id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> ItemException.NotFound.byId(id));
        return itemMapper.toDto(item);
    }

    public List<ItemDto> getAllItems() {
        List<Item> list = itemRepository.findAll();
        return list.stream()
                .map(itemMapper::toDto)
                .toList();
    }

    public List<ItemDto> getActiveAuctions() {
        List<Item> list = itemRepository.findAllByStatus(AuctionStatus.ACTIVE);
        return list.stream()
                .map(itemMapper::toDto)
                .toList();
    }

    public List<ItemDto> getAllItemsBySellerId(Long sellerId) {
        if (!userRepository.existsById(sellerId)) {
            throw UserException.NotFound.byId(sellerId);
        }
        List<Item> list = itemRepository.findAllBySellerId(sellerId);   
        return list.stream()
                .map(itemMapper::toDto)
                .toList(); 
    }
    
    @Transactional
    public ItemDto createItem(ItemRequest request) {
        User seller = userRepository.findById(request.sellerId())
            .orElseThrow(() -> UserException.NotFound.byId(request.sellerId()));

        // auction can't be scheduled after more than 3 month and can't go longer than a month
        if (request.endTime().isBefore(request.startTime())
            || request.startTime().isAfter(OffsetDateTime.now().plusMonths(3))
            || request.endTime().isAfter(request.startTime().plusMonths(1))) 
        { 
            throw ItemException.InvalidRequest.duration();
        }

        Item newItem = Item.builder()
            .title(request.title())
            .description(request.description())
            .itemImageUrl(request.itemImageUrl())
            .startingPrice(request.startingPrice())
            .startTime(request.startTime())
            .endTime(request.endTime())
            .status(AuctionStatus.PENDING)
            .currentHighestBid(request.startingPrice())
            .build();
        newItem.setSeller(seller);
        itemRepository.save(newItem);
        
        log.info("Auction item : {} created for user : {}", newItem.getTitle(), seller.getUsername());

        return itemMapper.toDto(newItem);
    }

    @Transactional//to-do after adding security, get user id from it not as parameter
    public ItemDto updateItem(Long itemId, Long userId, ItemUpdateRequest request) {
        Item updatedItem = itemRepository.findById(itemId)
            .orElseThrow(() -> ItemException.NotFound.byId(itemId));

        if (!updatedItem.getSeller().getId().equals(userId)) {
            throw ItemException.Unauthorized.notOwner();
        }

        if (bidRepository.countByItemId(itemId) > 0) {
            throw ItemException.InvalidState.hasBids();
        }

        if (updatedItem.getEndTime().isAfter(OffsetDateTime.now().plusWeeks(4))) {
            throw ItemException.InvalidRequest.duration();
        }

        updatedItem.setTitle(request.title());
        updatedItem.setDescription(request.description());
        updatedItem.setItemImageUrl(request.itemImageUrl());
        updatedItem.setStartingPrice(request.startingPrice());
        updatedItem.setStartTime(request.startTime());
        updatedItem.setEndTime(request.endTime());
        updatedItem.setCurrentHighestBid(request.startingPrice());
        itemRepository.save(updatedItem);

        log.info("Auction item : {} updated", updatedItem.getTitle());

        return itemMapper.toDto(updatedItem);
    }

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

        log.info("Auction item : {} deleted", deletedItem.getTitle());

        itemRepository.delete(deletedItem);
    }

    @Transactional
    public int startAuction() {
        return itemRepository.startPendingItems(OffsetDateTime.now());
    }

    @Transactional
    public int endAuction() {
        // update all expired items to CLOSED then fetch them
        int count = itemRepository.closeExpiredItems(OffsetDateTime.now());
        List<Item> closedItems = itemRepository.findAllByStatus(AuctionStatus.CLOSED);
        for (Item item : closedItems) {
            processAuctionItem(item); 
        }
        return count;
    }

    private void processAuctionItem(Item item) {
        // check if a top bid exist and update the winner and status accordingly
        bidRepository.findTopByItemOrderByAmountDesc(item)
            .ifPresentOrElse(
                (topBid) -> processSold(item, topBid), 
                () -> processUnsold(item)
            );
    }

    private void processSold(Item item, Bid bid) {
        item.setWinner(bid.getBidder());
        item.setStatus(AuctionStatus.SOLD);
        itemRepository.save(item);
        // send notifications for winner and seller
        eventPublisher.publishEvent(
            new EndAuctionEvent(
                item.getWinner().getEmail(), 
                item.getWinner().getUsername(), 
                item.getSeller().getEmail(),
                item.getSeller().getUsername(),
                item.getTitle(), 
                item.getCurrentHighestBid()
            )
        );
    }

    private void processUnsold(Item item) {
        item.setStatus(AuctionStatus.EXPIRED_UNSOLD);
        // send notifications for expired
        eventPublisher.publishEvent(
            new ExpiredAuctionEvent( 
                item.getSeller().getEmail(),
                item.getSeller().getUsername(),
                item.getTitle()
            )
        );
    }

    public void searchItems() {
        //to-do
    }

}
