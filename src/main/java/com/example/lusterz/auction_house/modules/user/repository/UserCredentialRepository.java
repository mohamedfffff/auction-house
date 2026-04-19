package com.example.lusterz.auction_house.modules.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lusterz.auction_house.modules.auth.model.AuthProviders;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.model.UserCredential;


public interface UserCredentialRepository extends JpaRepository<UserCredential, Long>{

    // args names must be the same as in entity
    Optional<UserCredential> findByUser(User user);
    Optional<UserCredential> findByUserAndProvider(User user, AuthProviders provider);
    boolean existsByUserAndProvider(User user, AuthProviders provider);
}
