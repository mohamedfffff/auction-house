package com.example.lusterz.auction_house.modules.user.service;

import java.math.BigDecimal;
import java.util.List;

import com.example.lusterz.auction_house.modules.user.dto.UserPrivateDto;
import com.example.lusterz.auction_house.modules.user.dto.UserPublicDto;
import com.example.lusterz.auction_house.modules.user.dto.UserRequest;

public interface UserService {
 
    UserPrivateDto getUserById(Long id);

    UserPublicDto getUserByName(String username);

    List<UserPrivateDto> getAllActiveUsers();

    List<UserPrivateDto> getAllUnactiveUsers();

    List<UserPrivateDto> getAllUsers();

    UserPrivateDto createUser(UserRequest userRequest);

    UserPrivateDto updateUser(Long id, UserRequest userRequest);

    void deactivateUser(Long id);

    //to-do
    void updateBalance(Long id, BigDecimal amount);
}   
