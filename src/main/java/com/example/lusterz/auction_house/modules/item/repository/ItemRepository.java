package com.example.lusterz.auction_house.modules.item.repository;


import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.lusterz.auction_house.modules.item.model.AuctionStatus;
import com.example.lusterz.auction_house.modules.item.model.Item;


public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByStatus(AuctionStatus status);
    List<Item> findAllBySellerId(Long sellerId);
    
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Item i SET i.status='ACTIVE' WHERE i.status='PENDING' AND i.startTime <= :now")
    int startPendingItems(@Param("now") OffsetDateTime now);
    
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Item i SET i.status='CLOSED' WHERE i.status='ACTIVE' AND i.endTime <= :now")
    int closeExpiredItems(@Param("now") OffsetDateTime now);
}
