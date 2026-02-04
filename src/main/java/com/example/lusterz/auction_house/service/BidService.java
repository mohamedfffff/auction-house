package com.example.lusterz.auction_house.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.lusterz.auction_house.Dto.BidRequest;
import com.example.lusterz.auction_house.exception.AuctionItemException;
import com.example.lusterz.auction_house.exception.BidException;
import com.example.lusterz.auction_house.exception.UserException;
import com.example.lusterz.auction_house.model.AuctionItem;
import com.example.lusterz.auction_house.model.Bid;
import com.example.lusterz.auction_house.model.User;
import com.example.lusterz.auction_house.model.enums.AuctionStatus;
import com.example.lusterz.auction_house.repository.AuctionItemRepository;
import com.example.lusterz.auction_house.repository.BidRepository;
import com.example.lusterz.auction_house.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class BidService {

    private final UserRepository userRepository;
    private final AuctionItemRepository itemRepository;
    private final BidRepository bidRepository;

    public BidService(UserRepository userRepository, AuctionItemRepository itemRepository, BidRepository bidRepository) {
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

    @Transactional
    public Bid createBid(Long userId, BidRequest bidRequest) {
        User bidder = userRepository.findById(userId)
            .orElseThrow(() -> UserException.NotFound.byId(userId));
        AuctionItem item = itemRepository.findById(bidRequest.auctionItemId())
            .orElseThrow(() -> AuctionItemException.NotFound.byId(bidRequest.auctionItemId()));

        if (!item.getSeller().getId().equals(userId)) {
            throw BidException.Unauthorized.isOwner();
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
        AuctionItem item = itemRepository.findById(itemId)
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
