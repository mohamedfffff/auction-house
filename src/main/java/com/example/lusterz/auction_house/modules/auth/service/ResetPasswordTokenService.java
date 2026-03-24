package com.example.lusterz.auction_house.modules.auth.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lusterz.auction_house.common.exception.AuthException;
import com.example.lusterz.auction_house.common.exception.UserException;
import com.example.lusterz.auction_house.modules.auth.model.ResetPasswordToken;
import com.example.lusterz.auction_house.modules.auth.repository.ResetPasswordTokenRepository;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ResetPasswordTokenService {
    
    @Value("${app.password.reset.expiration}")
    private Long expiration;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final UserRepository userRepository;

    public ResetPasswordToken getByToken(String token) {
        return resetPasswordTokenRepository.findByToken(token)
            .orElseThrow(() -> AuthException.ResetPasswordToken.notFound());
    }

    @Transactional
    public ResetPasswordToken generateToken(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> UserException.NotFound.byEmail(userEmail));

        ResetPasswordToken newToken = new ResetPasswordToken();
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setExpiration(Instant.now().plusMillis(expiration));
        newToken.setUser(user);

        return resetPasswordTokenRepository.save(newToken);
    }

    public boolean expired(ResetPasswordToken token) {
        return (token.getExpiration().isBefore(Instant.now()));
    }

    @Transactional
    public void deleteUsedToken(String token) {
        resetPasswordTokenRepository.deleteByToken(token);
    }

    @Transactional
    public int deleteExpiredTokens() {
        return resetPasswordTokenRepository.deleteExpired(Instant.now());
    }
}
