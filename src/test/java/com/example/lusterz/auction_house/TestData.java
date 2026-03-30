package com.example.lusterz.auction_house;

import java.util.UUID;

import org.mapstruct.factory.Mappers;

import com.example.lusterz.auction_house.modules.user.dto.UserPrivateDto;
import com.example.lusterz.auction_house.modules.user.dto.UserPublicDto;
import com.example.lusterz.auction_house.modules.user.mapper.UserMapper;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.model.UserRole;

public class TestData {

    private static final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

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

    public static UserPrivateDto testUserPrivateDto(User user) {
        return userMapper.toPrivateDto(user);
    }

    public static UserPublicDto testUserPublicDto(User user) {
        return userMapper.toPublicDto(user);
    }
}
