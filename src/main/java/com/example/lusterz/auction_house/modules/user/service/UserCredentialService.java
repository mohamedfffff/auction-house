package com.example.lusterz.auction_house.modules.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lusterz.auction_house.common.exception.AuthException;
import com.example.lusterz.auction_house.modules.auth.dto.RegisterRequest;
import com.example.lusterz.auction_house.modules.auth.model.AuthProviders;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.model.UserCredential;
import com.example.lusterz.auction_house.modules.user.repository.UserCredentialRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserCredentialService {
    
    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;

    public UserCredential getByUser(User user) {
        UserCredential userCredential = userCredentialRepository.findByUser(user)
            .orElseThrow(() -> AuthException.UserCredential.notFound());
        return userCredential;
    }

    @Transactional
    public void createLocalUserCredential(RegisterRequest request, User user) {
        UserCredential newUserCredential = new UserCredential();

        newUserCredential.setUser(user);
        newUserCredential.setProvider(AuthProviders.LOCAL);
        newUserCredential.setPassword(passwordEncoder.encode(request.password()));

        userCredentialRepository.save(newUserCredential);
    }

    @Transactional
    public void createOauth2UserCredential(User user, AuthProviders provider) {
        UserCredential newUserCredential = new UserCredential();

        newUserCredential.setUser(user);
        newUserCredential.setProvider(provider);

        userCredentialRepository.save(newUserCredential);
    }
}
