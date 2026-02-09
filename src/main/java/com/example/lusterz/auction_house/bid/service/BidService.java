package com.example.lusterz.auction_house.bid.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.lusterz.auction_house.bid.dto.BidRequest;
import com.example.lusterz.auction_house.bid.model.Bid;
import com.example.lusterz.auction_house.bid.repository.BidRepository;
import com.example.lusterz.auction_house.exception.AuctionItemException;
import com.example.lusterz.auction_house.exception.BidException;
import com.example.lusterz.auction_house.exception.UserException;
import com.example.lusterz.auction_house.item.model.AuctionStatus;
import com.example.lusterz.auction_house.item.model.Item;
import com.example.lusterz.auction_house.item.repository.ItemRepository;
import com.example.lusterz.auction_house.user.model.User;
import com.example.lusterz.auction_house.user.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class BidService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BidRepository bidRepository;

    public BidService(UserRepository userRepository, ItemRepository itemRepository, BidRepository bidRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bidRepository = bidRepository;
    }

    public Bid getBid(Long id) {
        return bidRepository.findById(id)
            .orElseThrow(() -> BidException.NotFound.byId(id));
    }

    public List<Bid> getAllBids() {
        return bidRepository.findAll();
    }

    public List<Bid> getAllByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw UserException.NotFound.byId(userId);
        }
        return bidRepository.findAllByUserId(userId);
    }

    public List<Bid> getAllByItemId(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw AuctionItemException.NotFound.byId(itemId);
        }
        return bidRepository.findAllByItemId(itemId);
    }

    @Transactional
    public Bid placeBid(Long userId, BidRequest bidRequest) {
        User bidder = userRepository.findById(userId)
            .orElseThrow(() -> UserException.NotFound.byId(userId));
        Item item = itemRepository.findById(bidRequest.auctionItemId())
            .orElseThrow(() -> AuctionItemException.NotFound.byId(bidRequest.auctionItemId()));

        if (!item.getSeller().getId().equals(userId)) {
            throw BidException.Unauthorized.isOwner();
        }

        if (bidder.getBalance().compareTo(bidRequest.amount()) < 0) {
            throw UserException.InsufficientFunds.balanceLessThanBid(bidRequest.amount());
        }

        if (!item.getStatus().equals(AuctionStatus.ACTIVE)) {
            throw AuctionItemException.InvalidState.notActive();
        }

        if (item.getCurrentHighestBid().compareTo(bidRequest.amount()) > 0 ) {
            throw BidException.InsufficientBid.lessThanHighest();
        }


        Bid newBid = new Bid();
        newBid.setAmount(bidRequest.amount());
        newBid.setBidder(bidder);
        newBid.setAuctionItem(item);

        bidRepository.save(newBid);

        return newBid;
    }

    @Transactional
    public void deleteBid(Long userId, Long itemId, Long bidId) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> AuctionItemException.NotFound.byId(itemId));
        Bid deletedBid = bidRepository.findById(bidId)
            .orElseThrow(() -> BidException.NotFound.byId(bidId));

        if (!deletedBid.getBidder().getId().equals(userId)) {
            throw BidException.Unauthorized.notOwner();
        }

        if (!item.getStatus().equals(AuctionStatus.ACTIVE)) {
            throw AuctionItemException.InvalidState.notActive();
        }


        bidRepository.delete(deletedBid);
    }
    
}
