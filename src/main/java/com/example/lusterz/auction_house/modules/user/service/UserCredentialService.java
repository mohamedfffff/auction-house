package com.example.lusterz.auction_house.modules.user.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lusterz.auction_house.common.exception.AuthException;
import com.example.lusterz.auction_house.common.exception.UserException;
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

    // this must return optional as it is used to set password to "" later in auth with google
    // and used in reset password logic in auth service
    public Optional<UserCredential> getByUserAndProvider(User user, AuthProviders provider) {
        return userCredentialRepository.findByUserAndProvider(user, provider);
    }

    @Transactional
    public void createLocalUserCredential(RegisterRequest request, User user) {
        if (userCredentialRepository.existsByUserAndProvider(user, AuthProviders.LOCAL)) {
            throw UserException.Credential.hasLocal();
        }

        UserCredential newUserCredential = new UserCredential();

        newUserCredential.setUser(user);
        newUserCredential.setProvider(AuthProviders.LOCAL);
        newUserCredential.setPassword(passwordEncoder.encode(request.password()));

        userCredentialRepository.save(newUserCredential);
    }

    @Transactional
    public void createLocalUserCredential(User user, String password) {
        if (userCredentialRepository.existsByUserAndProvider(user, AuthProviders.LOCAL)) {
            throw UserException.Credential.hasLocal();
        }
        
        UserCredential newUserCredential = new UserCredential();

        newUserCredential.setUser(user);
        newUserCredential.setProvider(AuthProviders.LOCAL);
        newUserCredential.setPassword(passwordEncoder.encode(password));

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
