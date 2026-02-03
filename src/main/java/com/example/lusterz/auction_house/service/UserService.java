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
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw UserException.AlreadyExists.byUsername(userRequest.getUsername());
        }
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw UserException.AlreadyExists.byEmail(userRequest.getEmail());
        }

        User newUser = new User();
        newUser.setUsername(userRequest.getUsername());
        newUser.setEmail(userRequest.getEmail());
        newUser.setPassword(userRequest.getPassword());
        newUser.setUserImageUrl(userRequest.getUserImageUrl());

        return userRepository.save(newUser);
    }

    @Transactional
    public User updateUser(Long id, UserRequest userRequest) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> UserException.NotFound.byId(id));

        if (!existingUser.getUsername().equals(userRequest.getUsername()) &&
             userRepository.existsByUsername(userRequest.getUsername())) {
            throw UserException.AlreadyExists.byUsername(userRequest.getUsername());
        }
        if (!existingUser.getEmail().equals(userRequest.getEmail()) &&
             userRepository.existsByEmail(userRequest.getEmail())) {
            throw UserException.AlreadyExists.byEmail(userRequest.getEmail());
        }

        existingUser.setUsername(userRequest.getUsername());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setPassword(userRequest.getPassword());
        existingUser.setUserImageUrl(userRequest.getUserImageUrl());
        existingUser.setBalance(userRequest.getBalance());

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
