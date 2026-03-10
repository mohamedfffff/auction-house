package com.example.lusterz.auction_house.modules.user.service;

import org.springframework.stereotype.Service;

import com.example.lusterz.auction_house.common.exception.UserException;
import com.example.lusterz.auction_house.modules.auth.model.AuthProviders;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.model.UserCredential;
import com.example.lusterz.auction_house.modules.user.repository.UserCredentialRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserCredentialService {
    
    private final UserCredentialRepository userCredentialRepository;

    public UserCredential getByUserAndProvider(User user, AuthProviders provider) {
        UserCredential userCredential = userCredentialRepository.findByUserAndProvider(user, AuthProviders.LOCAL)
            .orElseThrow(() -> UserException.Unauthorized.providerMismatch());
        return userCredential;
    }
}
