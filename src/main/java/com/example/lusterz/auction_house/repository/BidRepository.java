package com.example.lusterz.auction_house.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lusterz.auction_house.model.Bid;

public interface BidRepository extends JpaRepository<Bid, Long> {
    
}
