package com.example.lusterz.auction_house.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.lusterz.auction_house.model.AuctionItem;

public interface AuctionItemRepository extends JpaRepository<AuctionItem, Long> {

    @Query("SELECT a FROM AuctionItem LEFT JOIN FETCH a.bidHistory WHERE a.id = :id")
    Optional<AuctionItem> findByIdWithBidHistory(@Param("id") Long id);
}
