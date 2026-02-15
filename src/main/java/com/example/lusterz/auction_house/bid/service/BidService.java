package com.example.lusterz.auction_house.bid.service;

import java.util.List;


import com.example.lusterz.auction_house.bid.dto.BidDto;
import com.example.lusterz.auction_house.bid.dto.BidRequest;

public interface BidService {

    BidDto getBid(Long id);

    List<BidDto> getAllBids();

    List<BidDto> getAllBidsByBidderId(Long bidderId);

    List<BidDto> getAllBidsByItemId(Long itemId);

    BidDto placeBid(Long bidderId, Long itemId, BidRequest bidRequest);

    void deleteBid(Long bidderId, Long itemId, Long bidId);
}
