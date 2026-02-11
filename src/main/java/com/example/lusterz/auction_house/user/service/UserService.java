package com.example.lusterz.auction_house.user.service;

import java.math.BigDecimal;
import java.util.List;

import com.example.lusterz.auction_house.user.dto.UserPrivateDto;
import com.example.lusterz.auction_house.user.dto.UserPublicDto;
import com.example.lusterz.auction_house.user.dto.UserRequest;

public interface UserService {
 
    public UserPrivateDto getUserById(Long id);

    public UserPublicDto getUserByName(String username);

    public List<UserPrivateDto> getAllUsers();

    public UserPrivateDto createUser(UserRequest userRequest);

    public UserPrivateDto updateUser(Long id, UserRequest userRequest);

    public void deactivateUser(Long id);

    //to-do
    public void updateBalance(Long id, BigDecimal amount);
}   
