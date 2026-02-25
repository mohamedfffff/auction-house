package com.example.lusterz.auction_house.modules.user.service;

import java.math.BigDecimal;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lusterz.auction_house.common.exception.UserException;
import com.example.lusterz.auction_house.modules.user.dto.UserPrivateDto;
import com.example.lusterz.auction_house.modules.user.dto.UserPublicDto;
import com.example.lusterz.auction_house.modules.user.dto.UserUpdatePasswordRequest;
import com.example.lusterz.auction_house.modules.user.dto.UserUpdateRequest;
import com.example.lusterz.auction_house.modules.user.dto.UserUpdateRoleRequest;
import com.example.lusterz.auction_house.modules.user.mapper.UserMapper;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImp implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
 
    @Override
    public UserPrivateDto getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> UserException.NotFound.byId(id));
        return userMapper.toPrivateDto(user);
    }

    @Override
    public UserPublicDto getUserByName(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> UserException.NotFound.byUsername(username));
        return userMapper.toPublicDto(user); 
    }


    @Override
    public List<UserPrivateDto> getAllActiveUsers() {
        List<User> list = userRepository.findAllByActive(true);
        return list.stream()
                .map(userMapper::toPrivateDto)
                .toList();
    }

    @Override
    public List<UserPrivateDto> getAllUnactiveUsers() {
        List<User> list = userRepository.findAllByActive(false);
        return list.stream()
                .map(userMapper::toPrivateDto)
                .toList();
    }

    @Override
    public List<UserPrivateDto> getAllUsers() {
        List<User> list = userRepository.findAll();
        return list.stream()
                .map(userMapper::toPrivateDto)
                .toList();
    }


    @Transactional
    @Override
    public UserPrivateDto updateUser(Long id, UserUpdateRequest userRequest) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> UserException.NotFound.byId(id));

        if (!existingUser.getUsername().equals(userRequest.username()) &&
             userRepository.existsByUsername(userRequest.username())) {
            throw UserException.AlreadyExists.byUsername(userRequest.username());
        }
        if (!existingUser.getEmail().equals(userRequest.email()) &&
             userRepository.existsByEmail(userRequest.email())) {
            throw UserException.AlreadyExists.byEmail(userRequest.email());
        }

        existingUser.setUsername(userRequest.username());
        existingUser.setEmail(userRequest.email());
        existingUser.setUserImageUrl(userRequest.userImageUrl());

        userRepository.save(existingUser);

        return userMapper.toPrivateDto(existingUser);
    }

    @Transactional
    @Override
    public void deactivateUser(Long id) {
        User deletedUser = userRepository.findById(id)
            .orElseThrow(() -> UserException.NotFound.byId(id));

        //to-do check if request user id matches the id
        // Long currentUserId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        // if (!deletedUser.getId().equals(currentUserId)) {
        //     throw UserException.Unauthorized.notOwner();
        // }

        deletedUser.setActive(false);
        userRepository.save(deletedUser);
    }

    @Transactional
    @Override
    public void updatePassword(Long id, UserUpdatePasswordRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> UserException.NotFound.byId(id));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw UserException.PasswordMismatch.oldAndGiven();
        }

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw UserException.PasswordMismatch.newAndConfirm();
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void updateRole(Long id, UserUpdateRoleRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> UserException.NotFound.byId(id));

        user.setRole(request.role());
        userRepository.save(user);

        refreshSecurityContext(user);
    }

    @Transactional
    @Override
    public void updateBalance(Long id, BigDecimal amount) {
        //to-do 
    }

    private void refreshSecurityContext(User user) {
        //to-do
    }

}
