package com.example.lusterz.auction_house.modules.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lusterz.auction_house.modules.auth.model.RefreshToken;
import com.example.lusterz.auction_house.modules.user.model.User;

public interface RefreshTokenReposityory extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
