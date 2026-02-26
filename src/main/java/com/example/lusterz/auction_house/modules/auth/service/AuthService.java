package com.example.lusterz.auction_house.modules.auth.service;

import com.example.lusterz.auction_house.modules.auth.dto.AuthResponse;
import com.example.lusterz.auction_house.modules.auth.dto.LoginRequest;
import com.example.lusterz.auction_house.modules.auth.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    void verifyEmail();
     
    void refreshToken();
}
