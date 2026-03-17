package com.example.lusterz.auction_house.modules.auth.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lusterz.auction_house.common.exception.AuthException;
import com.example.lusterz.auction_house.common.exception.UserException;
import com.example.lusterz.auction_house.modules.auth.model.VerifyToken;
import com.example.lusterz.auction_house.modules.auth.repository.VerifyTokenRepository;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VerifyTokenService {

    @Value("${app.jwt.verify.expiration}")
    private Long expiration;
    private final VerifyTokenRepository verifyTokenRepository;
    private final UserRepository userRepository;

    public VerifyToken getByToken(String token) {
        return verifyTokenRepository.findByToken(token)
            .orElseThrow(() -> AuthException.VerifyToken.notFound());
    }

    @Transactional
    public VerifyToken generateToken(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> UserException.NotFound.byEmail(userEmail));

        VerifyToken newToken = new VerifyToken();
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setExpiration(Instant.now().plusMillis(expiration));
        newToken.setUser(user);

        return verifyTokenRepository.save(newToken);
    }

    public boolean expired(VerifyToken token) {
        return (token.getExpiration().isBefore(Instant.now()));
    }

    @Transactional
    public void deleteUsedToken(String token) {
        verifyTokenRepository.deleteByToken(token);
    }

    @Transactional
    public int deleteExpiredTokens() {
        return verifyTokenRepository.deleteExpired(Instant.now());
    }
}
