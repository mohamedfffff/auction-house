package com.example.lusterz.auction_house.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lusterz.auction_house.model.AuctionItem;

public interface AuctionItemRepository extends JpaRepository<AuctionItem, Long> {
    
}
