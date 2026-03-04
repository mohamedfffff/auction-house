package com.example.lusterz.auction_house.modules.auth.service;

import com.example.lusterz.auction_house.modules.auth.model.RefreshToken;

public interface RefreshTokenService {
    

    public RefreshToken getRefreshToken(String token);

    public RefreshToken generateToken(Long userId);

    public RefreshToken rotateToken(String oldToken);

    public boolean expired(RefreshToken token);

    public void deleteExpiredTokens();
}
