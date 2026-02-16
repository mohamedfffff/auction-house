package com.example.lusterz.auction_house.modules.user.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lusterz.auction_house.modules.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findAllByActive(boolean active);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
