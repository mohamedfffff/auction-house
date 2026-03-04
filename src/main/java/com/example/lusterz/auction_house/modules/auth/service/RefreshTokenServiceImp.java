package com.example.lusterz.auction_house.modules.auth.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lusterz.auction_house.common.exception.AuthException;
import com.example.lusterz.auction_house.common.exception.UserException;
import com.example.lusterz.auction_house.modules.auth.model.RefreshToken;
import com.example.lusterz.auction_house.modules.auth.repository.RefreshTokenReposityory;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImp implements RefreshTokenService{
    
    @Value("${app.jwt.refresh.expiration}")
    private Long refreshExpiration;
    private final RefreshTokenReposityory refreshTokenReposityory;
    private final UserRepository userRepository;

    @Override
    public RefreshToken getRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenReposityory.findByToken(token)
            .orElseThrow(() -> AuthException.RefreshToken.notFound());
        return refreshToken;
    }

    @Transactional
    @Override
    public RefreshToken generateToken(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> UserException.NotFound.byId(userId));

        // make sure user has no token
        // because if the user logout and re-login
        refreshTokenReposityory.deleteByUser(user);

        RefreshToken token = RefreshToken.builder()
            .token(UUID.randomUUID().toString())
            .expiration(Instant.now().plusMillis(refreshExpiration))
            .user(user)
            .build();

        return refreshTokenReposityory.save(token);
    }

    @Transactional
    @Override
    public RefreshToken rotateToken(String oldToken) {
        RefreshToken token = refreshTokenReposityory.findByToken(oldToken)
            .orElseThrow(() -> AuthException.RefreshToken.notFound());

        if (expired(token)) {
            throw AuthException.RefreshToken.expired();
        }
        
        User user = token.getUser();
        refreshTokenReposityory.delete(token);

        return generateToken(user.getId());
         
    }

    @Override
    public boolean expired(RefreshToken token) {
        return (token.getExpiration().isBefore(Instant.now()));
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        refreshTokenReposityory.deleteExpired(Instant.now());
    }
}
