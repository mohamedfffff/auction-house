package com.example.lusterz.auction_house.item.repository;


import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.lusterz.auction_house.item.model.AuctionStatus;
import com.example.lusterz.auction_house.item.model.Item;

import jakarta.transaction.Transactional;


public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByStatus(AuctionStatus status);
    List<Item> findAllBySellerId(Long sellerId);
    @Modifying
    @Transactional
    @Query("UPDATE Item i SET i.status='CLOSED' WHERE i.status='ACTIVE' AND i.endTime <= :now")
    int closeExpiredItems(@Param("now") LocalDateTime now);
}
