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
    @Modifying(clearAutomatically = true)
    @Query("DELETE VerifyToken t WHERE t.expiration <= :time")
    int deleteExpired(@Param("time") Instant time);

}
