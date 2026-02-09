package com.example.lusterz.auction_house.user.service;

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
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }
 
    @PreAuthorize("hasRole('ADMIN')")
    public UserPrivateDto getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> UserException.NotFound.byId(id));
        return userMapper.toPrivateDto(user);
    }

    public UserPublicDto getUserByName(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> UserException.NotFound.byUsername(username));
        return userMapper.toPublicDto(user); 
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();  
    }

    @Transactional
    public User createUser(UserRequest userRequest) {
        if (userRepository.existsByUsername(userRequest.username())) {
            throw UserException.AlreadyExists.byUsername(userRequest.username());
        }
        if (userRepository.existsByEmail(userRequest.email())) {
            throw UserException.AlreadyExists.byEmail(userRequest.email());
        }

        User newUser = new User();
        newUser.setUsername(userRequest.username());
        newUser.setEmail(userRequest.email());
        newUser.setPassword(userRequest.password());
        newUser.setUserImageUrl(userRequest.userImageUrl());

        return userRepository.save(newUser);
    }

    @Transactional
    public User updateUser(Long id, UserRequest userRequest) {
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
        existingUser.setPassword(userRequest.password());
        existingUser.setUserImageUrl(userRequest.userImageUrl());
        existingUser.setBalance(userRequest.balance());

        return userRepository.save(existingUser);
    }

    @Transactional
    public void deactivateUser(Long id) {
        User deletedUser = userRepository.findById(id)
            .orElseThrow(() -> UserException.NotFound.byId(id));

        if (!deletedUser.getId().equals(id)) {
            throw UserException.Unauthorized.notOwner();
        }

        deletedUser.setActive(false);
        userRepository.save(deletedUser);
    }

    @Transactional
    public void updateBalance() {
        //to-do
    }
}
