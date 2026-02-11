package com.example.lusterz.auction_house.user.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lusterz.auction_house.exception.UserException;
import com.example.lusterz.auction_house.user.dto.UserPrivateDto;
import com.example.lusterz.auction_house.user.dto.UserPublicDto;
import com.example.lusterz.auction_house.user.dto.UserRequest;
import com.example.lusterz.auction_house.user.mapper.UserMapper;
import com.example.lusterz.auction_house.user.model.User;
import com.example.lusterz.auction_house.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserServiceImp implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImp(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }
 
    @PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<UserPrivateDto> getAllUsers() {
        List<User> list = userRepository.findAll();
        return list.stream()
                .map(userMapper::toPrivateDto)
                .toList();
    }

    @Transactional
    @Override
    public UserPrivateDto createUser(UserRequest userRequest) {
        if (userRepository.existsByUsername(userRequest.username())) {
            throw UserException.AlreadyExists.byUsername(userRequest.username());
        }
        if (userRepository.existsByEmail(userRequest.email())) {
            throw UserException.AlreadyExists.byEmail(userRequest.email());
        }

        User newUser = new User();
        newUser.setUsername(userRequest.username());
        newUser.setEmail(userRequest.email());
        newUser.setPassword(userRequest.password());//to-do hash the password
        newUser.setUserImageUrl(userRequest.userImageUrl());

        userRepository.save(newUser);

        return userMapper.toPrivateDto(newUser);
    }

    @Transactional
    @Override
    public UserPrivateDto updateUser(Long id, UserRequest userRequest) {
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
        existingUser.setPassword(userRequest.password());//to-do hash the password
        existingUser.setUserImageUrl(userRequest.userImageUrl());
        existingUser.setBalance(userRequest.balance());

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
    public void updateBalance(Long id, BigDecimal amount) {
        //to-do
    }
}
