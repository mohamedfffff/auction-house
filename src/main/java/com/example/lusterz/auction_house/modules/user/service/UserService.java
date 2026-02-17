package com.example.lusterz.auction_house.modules.user.service;

import java.math.BigDecimal;
import java.util.List;

import com.example.lusterz.auction_house.modules.user.dto.UserPrivateDto;
import com.example.lusterz.auction_house.modules.user.dto.UserPublicDto;
import com.example.lusterz.auction_house.modules.user.dto.UserUpdatePasswordRequest;
import com.example.lusterz.auction_house.modules.user.dto.UserUpdateRequest;
import com.example.lusterz.auction_house.modules.user.dto.UserUpdateRoleRequest;
import com.example.lusterz.auction_house.modules.user.model.UserRole;
import com.example.lusterz.auction_house.modules.user.dto.UserCreateRequest;

public interface UserService {
 
    UserPrivateDto getUserById(Long id);

    UserPublicDto getUserByName(String username);

    List<UserPrivateDto> getAllActiveUsers();

    List<UserPrivateDto> getAllUnactiveUsers();

    List<UserPrivateDto> getAllUsers();

    UserPrivateDto createUser(UserCreateRequest request);

    UserPrivateDto updateUser(Long id, UserUpdateRequest request);

    void deactivateUser(Long id);

    void updatePassword(Long id, UserUpdatePasswordRequest request);

    void updateRole(Long id, UserUpdateRoleRequest newRole);

    //to-do
    void updateBalance(Long id, BigDecimal amount);

}   
