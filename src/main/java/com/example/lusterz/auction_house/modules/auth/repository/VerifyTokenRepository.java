package com.example.lusterz.auction_house.modules.auth.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.lusterz.auction_house.modules.auth.model.VerifyToken;

public interface VerifyTokenRepository extends JpaRepository<VerifyToken, Long> {
    
    Optional<VerifyToken> findByToken(String token);

    // custom query is faster cause hibernate load the entity then perform the deletion
    @Modifying
    @Query("DELETE FROM VerifyToken v WHERE v.token = :token")
    void deleteByToken(@Param("token") String token);

    // this query is called by scheduler 
    // so cealrAutomatically is used to ensure hibernate delete the data loaded into memory
    // unlike normal service which ends transaction
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM VerifyToken t WHERE t.expiration <= :time")
    int deleteExpired(@Param("time") Instant time);

}
