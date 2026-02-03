package com.example.lusterz.auction_house.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.example.lusterz.auction_house.Dto.UserRequest;
import com.example.lusterz.auction_house.exception.UserException;
import com.example.lusterz.auction_house.model.User;
import com.example.lusterz.auction_house.repository.UserRepository;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> UserException.NotFound.byId(id));  
    }

    public User getUserByName(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> UserException.NotFound.byUsername(username));  
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();  
    }

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
        deletedUser.setActive(false);
        userRepository.save(deletedUser);
    }
}
