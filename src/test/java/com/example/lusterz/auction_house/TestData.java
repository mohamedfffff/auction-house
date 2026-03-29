package com.example.lusterz.auction_house;

import java.util.UUID;

import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.model.UserRole;

public class TestData {

    public static User testUser(Long id, boolean active) {
        return new User(
                id,
                "testUser_" + UUID.randomUUID().toString().substring(0,5),
                "testEmail" + UUID.randomUUID().toString().substring(0,5) + "@gmail.com",
                "Image.com",
                active,
                UserRole.ROLE_USER,
                null,
                null,
                null
        );
    }
}
