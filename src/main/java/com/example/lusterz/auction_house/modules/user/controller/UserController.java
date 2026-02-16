package com.example.lusterz.auction_house.modules.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lusterz.auction_house.modules.user.dto.UserPrivateDto;
import com.example.lusterz.auction_house.modules.user.dto.UserPublicDto;
import com.example.lusterz.auction_house.modules.user.dto.UserRequest;
import com.example.lusterz.auction_house.modules.user.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/id/{id}")
    public UserPrivateDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/name/{name}")
    public UserPublicDto getUserByName(@PathVariable String name) {
        return userService.getUserByName(name); 
    }

    @GetMapping
    public List<UserPrivateDto> getAllUsers(@RequestParam(required = false) Boolean active) {
        if (active != null && active) return userService.getAllActiveUsers();
        if (active != null && !active) return userService.getAllUnactiveUsers();
        return userService.getAllUsers();
    }

    @PostMapping()
    public ResponseEntity<UserPrivateDto> createUser(@Valid @RequestBody UserRequest userRequest) {
        UserPrivateDto newUser = userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PutMapping("/{id}")
    public UserPrivateDto updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
        return userService.updateUser(id, userRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    //to-do
    public void updateBalance() {}
    
}
