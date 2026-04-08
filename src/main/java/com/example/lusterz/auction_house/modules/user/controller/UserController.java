package com.example.lusterz.auction_house.modules.user.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lusterz.auction_house.modules.user.dto.UserPrivateDto;
import com.example.lusterz.auction_house.modules.user.dto.UserPublicDto;
import com.example.lusterz.auction_house.modules.user.dto.UserUpdateEmailRequest;
import com.example.lusterz.auction_house.modules.user.dto.UserUpdatePasswordRequest;
import com.example.lusterz.auction_house.modules.user.dto.UserUpdateProfileImageRequest;
import com.example.lusterz.auction_house.modules.user.dto.UserUpdateRoleRequest;
import com.example.lusterz.auction_house.modules.user.dto.UserUpdateUsernameRequest;
import com.example.lusterz.auction_house.modules.user.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

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


    @PutMapping("/{id}/username")
    public String updateUsername(@PathVariable Long id, @Valid @RequestBody UserUpdateUsernameRequest request) {
        return userService.updateUsername(id, request);
    }

    @PutMapping("/{id}/email")
    public String updateEmail(@PathVariable Long id, @Valid @RequestBody UserUpdateEmailRequest request) {
        return userService.updateEmail(id, request);
    }

    @PutMapping("/{id}/imageUrl")
    public String updateImageUrl(@PathVariable Long id, @Valid @RequestBody UserUpdateProfileImageRequest request) {
        return userService.updateProfileImage(id, request);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<Void> updateRole(@PathVariable Long id, @Valid @RequestBody UserUpdateRoleRequest role) {
        userService.updateRole(id, role);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @Valid @RequestBody UserUpdatePasswordRequest request) {
        userService.updatePassword(id, request);
        System.out.println(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
    
}
