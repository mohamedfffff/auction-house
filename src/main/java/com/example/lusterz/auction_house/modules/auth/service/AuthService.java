package com.example.lusterz.auction_house.modules.auth.service;

import com.example.lusterz.auction_house.modules.auth.dto.JwtResponse;
import com.example.lusterz.auction_house.modules.auth.dto.LoginRequest;
import com.example.lusterz.auction_house.modules.auth.dto.RegisterRequest;

public interface AuthService {

    JwtResponse register(RegisterRequest request);

    JwtResponse login(LoginRequest request);

    void verifyEmail();
     
    void refreshToken();
}
