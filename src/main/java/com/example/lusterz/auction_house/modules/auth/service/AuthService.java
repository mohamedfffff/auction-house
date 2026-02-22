package com.example.lusterz.auction_house.modules.auth.service;

import com.example.lusterz.auction_house.modules.auth.dto.LoginRequest;
import com.example.lusterz.auction_house.modules.auth.dto.RegisterRequest;
import com.example.lusterz.auction_house.modules.user.dto.UserPrivateDto;

public interface AuthService {

    UserPrivateDto register(RegisterRequest request);

    void login(LoginRequest request);

    void verifyEmail();
     
    void refreshToken();
}
