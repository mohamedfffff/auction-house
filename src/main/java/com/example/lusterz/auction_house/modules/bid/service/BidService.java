package com.example.lusterz.auction_house.modules.bid.service;

import java.util.List;

import com.example.lusterz.auction_house.modules.bid.dto.BidDto;
import com.example.lusterz.auction_house.modules.bid.dto.BidRequest;

public interface BidService {

    BidDto getBid(Long id);

    List<BidDto> getAllBids();

    List<BidDto> getAllBidsByBidderId(Long bidderId);

    List<BidDto> getAllBidsByItemId(Long itemId);

    BidDto placeBid(BidRequest bidRequest);

    void deleteBid(Long bidderId, Long itemId, Long bidId);
}
