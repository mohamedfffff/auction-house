package com.example.lusterz.auction_house.repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.lusterz.auction_house.model.AuctionItem;
import com.example.lusterz.auction_house.model.enums.AuctionStatus;

public interface AuctionItemRepository extends JpaRepository<AuctionItem, Long> {
    boolean existsById(Long id);
    List<AuctionItem> findAllByStatus(AuctionStatus status);
    @Query("SELECT a FROM AuctionItem a WHERE a.seller.id = :userId")
    List<AuctionItem> findAllByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE AuctionItem i SET i.status='CLOSED' WHERE i.status='ACTIVE' AND i.endTime <= :now")
    int closeExpiredItems(@Param("now") LocalDateTime now);
}
