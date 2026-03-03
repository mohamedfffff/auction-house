package com.example.lusterz.auction_house.modules.auth.service;

import com.example.lusterz.auction_house.modules.auth.dto.AuthResponse;
import com.example.lusterz.auction_house.modules.auth.dto.LoginRequest;
import com.example.lusterz.auction_house.modules.auth.dto.RefreshTokenRequest;
import com.example.lusterz.auction_house.modules.auth.dto.RefreshTokenResponse;
import com.example.lusterz.auction_house.modules.auth.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    RefreshTokenResponse refreshAccessToken(RefreshTokenRequest token);
    
    void verifyEmail();
}
