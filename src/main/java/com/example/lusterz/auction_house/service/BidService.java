package com.example.lusterz.auction_house.service;

import org.springframework.stereotype.Service;

import com.example.lusterz.auction_house.exception.BidException;
import com.example.lusterz.auction_house.model.Bid;
import com.example.lusterz.auction_house.repository.BidRepository;

@Service
public class BidService {

    private final BidRepository bidRepository;

    public BidService(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    public Bid getBid(Long id) {
        return bidRepository.findById(id)
            .orElseThrow(() -> BidException.NotFound.byId(id));
    }
    
}
