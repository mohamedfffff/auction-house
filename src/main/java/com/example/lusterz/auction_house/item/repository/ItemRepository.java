package com.example.lusterz.auction_house.item.repository;


import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.lusterz.auction_house.item.model.AuctionStatus;
import com.example.lusterz.auction_house.item.model.Item;


public interface ItemRepository extends JpaRepository<Item, Long> {
    boolean existsById(Long id);
    List<Item> findAllByStatus(AuctionStatus status);
    @Query("SELECT a FROM AuctionItem a WHERE a.seller.id = :userId")
    List<Item> findAllByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE AuctionItem i SET i.status='CLOSED' WHERE i.status='ACTIVE' AND i.endTime <= :now")
    int closeExpiredItems(@Param("now") LocalDateTime now);
}
