package com.example.lusterz.auction_house;

import com.example.lusterz.auction_house.modules.user.dto.UserPrivateDto;
import com.example.lusterz.auction_house.modules.user.dto.UserPublicDto;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.model.UserRole;

public class TestData {

    public static User testUser() {
        return new User(
                1L,
                "Alex",
                "AlexEmail@gmail.com",
                "AlexImage.com",
                true,
                UserRole.ROLE_USER,
                null,
                null,
                null
        );
    }

    public static UserPrivateDto testUserPrivateDto() {
        return new UserPrivateDto(
                1L,
                "Alex",
                "AlexEmail@gmail.com",
                "AlexImage.com",
                UserRole.ROLE_USER,
                null,
                null
        );
    }

    public static UserPublicDto testUserPublicDto() {
        return new UserPublicDto(
                "Alex",
                "AlexEmail@gmail.com",
                null
        );
    }
}
