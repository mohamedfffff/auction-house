package com.example.lusterz.auction_house.user.service;

import java.math.BigDecimal;
import java.util.List;

import com.example.lusterz.auction_house.user.dto.UserPrivateDto;
import com.example.lusterz.auction_house.user.dto.UserPublicDto;
import com.example.lusterz.auction_house.user.dto.UserRequest;

public interface UserService {
 
    UserPrivateDto getUserById(Long id);

    UserPublicDto getUserByName(String username);

    List<UserPrivateDto> getAllUsers();

    UserPrivateDto createUser(UserRequest userRequest);

    UserPrivateDto updateUser(Long id, UserRequest userRequest);

    void deactivateUser(Long id);

    //to-do
    void updateBalance(Long id, BigDecimal amount);
}   
