package com.example.lusterz.auction_house.modules.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lusterz.auction_house.modules.auth.dto.registerRequest;
import com.example.lusterz.auction_house.modules.auth.service.AuthService;
import com.example.lusterz.auction_house.modules.user.dto.UserPrivateDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<UserPrivateDto> register(@Valid @RequestBody registerRequest userRequest) {
        UserPrivateDto newUser = authService.register(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
}
