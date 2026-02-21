package com.example.lusterz.auction_house.modules.bid.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lusterz.auction_house.common.exception.ItemException;
import com.example.lusterz.auction_house.common.exception.BidException;
import com.example.lusterz.auction_house.common.exception.UserException;
import com.example.lusterz.auction_house.modules.bid.dto.BidDto;
import com.example.lusterz.auction_house.modules.bid.dto.BidRequest;
import com.example.lusterz.auction_house.modules.bid.mapper.BidMapper;
import com.example.lusterz.auction_house.modules.bid.model.Bid;
import com.example.lusterz.auction_house.modules.bid.repository.BidRepository;
import com.example.lusterz.auction_house.modules.item.model.AuctionStatus;
import com.example.lusterz.auction_house.modules.item.model.Item;
import com.example.lusterz.auction_house.modules.item.repository.ItemRepository;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.repository.UserRepository;


@Service
@Transactional(readOnly = true)
public class BidServiceImp implements BidService{

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BidRepository bidRepository;
    private final BidMapper bidMapper;

    public BidServiceImp(UserRepository userRepository, ItemRepository itemRepository, BidRepository bidRepository, BidMapper bidMapper) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bidRepository = bidRepository;
        this.bidMapper = bidMapper;
    }

    @Override
    public BidDto getBid(Long id) {
        Bid bid = bidRepository.findById(id)
            .orElseThrow(() -> BidException.NotFound.byId(id));
        return bidMapper.toDto(bid);
    }

    @Override
    public List<BidDto> getAllBids() {
        return bidRepository.findAll()
                .stream()
                .map(bidMapper::toDto)
                .toList();
    }

    @Override
    public List<BidDto> getAllBidsByBidderId(Long bidderId) {
        if (!userRepository.existsById(bidderId)) {
            throw UserException.NotFound.byId(bidderId);
        }
        return bidRepository.findAllByBidderId(bidderId)
                .stream()
                .map(bidMapper::toDto)
                .toList();
    }

    @Override
    public List<BidDto> getAllBidsByItemId(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw ItemException.NotFound.byId(itemId);
        }
        return bidRepository.findAllByItemId(itemId)
                .stream()
                .map(bidMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public BidDto placeBid(BidRequest bidRequest) {
        User bidder = userRepository.findById(bidRequest.bidderId())
            .orElseThrow(() -> UserException.NotFound.byId(bidRequest.bidderId()));
        Item item = itemRepository.findById(bidRequest.itemId())
            .orElseThrow(() -> ItemException.NotFound.byId(bidRequest.itemId()));

        if (item.getSeller().getId().equals(bidRequest.bidderId())) {
            throw BidException.Unauthorized.isOwner();
        }

        if (bidder.getBalance().compareTo(bidRequest.amount()) < 0) {
            throw UserException.InsufficientFunds.balanceLessThanBid(bidRequest.amount());
        }

        if (!item.getStatus().equals(AuctionStatus.ACTIVE)) {
            throw ItemException.InvalidState.notActive();
        }

        if (item.getCurrentHighestBid().compareTo(bidRequest.amount()) > 0 ) {
            throw BidException.InsufficientBid.lessThanHighest();
        }


        Bid newBid = new Bid();
        newBid.setAmount(bidRequest.amount());
        newBid.setBidder(bidder);
        newBid.setItem(item);
        newBid.setBidTime(OffsetDateTime.now());

        // update the highest bid on item entity
        item.setCurrentHighestBid(bidRequest.amount());

        bidRepository.save(newBid);
        return bidMapper.toDto(newBid);
    }

    @Override
    @Transactional
    public void deleteBid(Long bidderId, Long itemId, Long bidId) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> ItemException.NotFound.byId(itemId));
        Bid deletedBid = bidRepository.findById(bidId)
            .orElseThrow(() -> BidException.NotFound.byId(bidId));

        if (!deletedBid.getBidder().getId().equals(bidderId)) {
            throw BidException.Unauthorized.notOwner();
        }

        if (!item.getStatus().equals(AuctionStatus.ACTIVE)) {
            throw ItemException.InvalidState.notActive();
        }


        bidRepository.delete(deletedBid);

        // if the deleted bid is the highest then we need to update the item
        if (deletedBid.getAmount().equals(item.getCurrentHighestBid())) {
            updateHighestBid(item);
        }
        
    }
    
    private void updateHighestBid(Item item) {
        BigDecimal highest = bidRepository.findTopByItemOrderByAmountDesc(item)
            .map(Bid::getAmount)
            .orElse(item.getStartingPrice());

        item.setCurrentHighestBid(highest);
    }

}
