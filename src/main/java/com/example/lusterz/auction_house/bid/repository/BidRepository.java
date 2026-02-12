package com.example.lusterz.auction_house.bid.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lusterz.auction_house.bid.model.Bid;
import com.example.lusterz.auction_house.item.model.Item;


public interface BidRepository extends JpaRepository<Bid, Long> {

    int countByItemId(Long itemId);
    Optional<Bid> findTopByItemOrderByAmountDesc(Item item);
    List<Bid> findAllByBidderId(Long userId);
    List<Bid> findAllByItemId(Long itemId);
    
}
