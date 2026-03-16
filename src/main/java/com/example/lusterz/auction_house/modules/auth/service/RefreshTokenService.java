package com.example.lusterz.auction_house.modules.auth.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lusterz.auction_house.common.exception.AuthException;
import com.example.lusterz.auction_house.modules.auth.model.RefreshToken;
import com.example.lusterz.auction_house.modules.auth.repository.RefreshTokenReposityory;
import com.example.lusterz.auction_house.modules.user.model.User;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    
    // variables with @Value annotation can't be final
    // spring first initialize it then inject the value later
    @Value("${app.jwt.refresh.expiration}")
    private Long refreshExpiration;
    private final RefreshTokenReposityory refreshTokenReposityory;

    public RefreshToken getRefreshToken(String token) {
        return refreshTokenReposityory.findByToken(token)
            .orElseThrow(() -> AuthException.RefreshToken.notFound());
    }

    @Transactional
    public RefreshToken generateToken(User user) {

        // make sure user has no token
        // because if the user logout and re-login
        refreshTokenReposityory.deleteByUser(user);

        RefreshToken newToken = new RefreshToken();
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setExpiration(Instant.now().plusMillis(refreshExpiration));
        newToken.setUser(user);

        return refreshTokenReposityory.save(newToken);
    }

    @Transactional
    public RefreshToken rotateToken(String oldToken) {
        RefreshToken token = refreshTokenReposityory.findByToken(oldToken)
            .orElseThrow(() -> AuthException.RefreshToken.notFound());

        if (expired(token)) {
            throw AuthException.RefreshToken.expired();
        }
        
        User user = token.getUser();
        refreshTokenReposityory.delete(token);

        return generateToken(user);
         
    }

    public boolean expired(RefreshToken token) {
        return (token.getExpiration().isBefore(Instant.now()));
    }

    @Transactional
    public int deleteExpiredTokens() {
        return refreshTokenReposityory.deleteExpired(Instant.now());
    }
}
