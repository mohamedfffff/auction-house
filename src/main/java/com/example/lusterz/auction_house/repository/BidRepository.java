package com.example.lusterz.auction_house.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.lusterz.auction_house.model.AuctionItem;
import com.example.lusterz.auction_house.model.Bid;

import io.lettuce.core.dynamic.annotation.Param;

public interface BidRepository extends JpaRepository<Bid, Long> {

    Optional<Bid> findTopByItemOrderByAmountDesc(AuctionItem item);
    @Query("SELECT b FROM Bid b WHERE b.bidder.id = :userId")
    List<Bid> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT b FROM Bid b WHERE b.auctionItem.id = :itemId")
    List<Bid> findAllByItemId(@Param("itemId") Long itemId);
    
}
