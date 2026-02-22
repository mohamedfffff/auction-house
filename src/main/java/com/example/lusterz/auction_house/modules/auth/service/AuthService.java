package com.example.lusterz.auction_house.modules.auth.service;

import com.example.lusterz.auction_house.modules.auth.dto.registerRequest;
import com.example.lusterz.auction_house.modules.user.dto.UserPrivateDto;

public interface AuthService {

    UserPrivateDto register(registerRequest request);

    void login();

    void verifyEmail();
     
    void refreshToken();
}
