package com.example.lusterz.auction_house.service;

import java.util.List;

import com.example.lusterz.auction_house.Dto.UserRequest;
import com.example.lusterz.auction_house.exception.EmailAlreadyExistsException;
import com.example.lusterz.auction_house.exception.UserNotFoundException;
import com.example.lusterz.auction_house.exception.UsernameAlreadyExistsException;
import com.example.lusterz.auction_house.model.User;
import com.example.lusterz.auction_house.repository.UserRepository;

public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));  
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();  
    }

    public User saveUser(UserRequest userRequest) {
        if (userRepository.exexistsByUsername(userRequest.getUsername())) {
            throw new UsernameAlreadyExistsException(userRequest.getUsername());
        }
        if (userRepository.exexistsByEmail(userRequest.getEmail())) {
            throw new EmailAlreadyExistsException(userRequest.getEmail());
        }

        User newUser = new User();
        newUser.setUsername(userRequest.getUsername());
        newUser.setEmail(userRequest.getEmail());
        newUser.setPassword(userRequest.getPassword());
        newUser.setUserImageUrl(userRequest.getUserImageUrl());

        return userRepository.save(newUser);
    }

    public User updateUser(Long id, UserRequest userRequest) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        if (!existingUser.getUsername().equals(userRequest.getUsername()) &&
             userRepository.exexistsByUsername(userRequest.getUsername())) {
            throw new UsernameAlreadyExistsException(userRequest.getUsername());
        }
        if (!existingUser.getEmail().equals(userRequest.getEmail()) &&
             userRepository.exexistsByEmail(userRequest.getEmail())) {
            throw new EmailAlreadyExistsException(userRequest.getEmail());
        }

        existingUser.setUsername(userRequest.getUsername());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setPassword(userRequest.getPassword());
        existingUser.setUserImageUrl(userRequest.getUserImageUrl());

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        User deletedUser = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(deletedUser);
    }
}
