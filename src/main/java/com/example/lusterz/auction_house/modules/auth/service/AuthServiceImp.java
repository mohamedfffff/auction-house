package com.example.lusterz.auction_house.modules.auth.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lusterz.auction_house.common.exception.AuthException;
import com.example.lusterz.auction_house.common.exception.UserException;
import com.example.lusterz.auction_house.common.util.JwtUtils;
import com.example.lusterz.auction_house.modules.auth.dto.JwtResponse;
import com.example.lusterz.auction_house.modules.auth.dto.LoginRequest;
import com.example.lusterz.auction_house.modules.auth.dto.RegisterRequest;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.model.UserRole;
import com.example.lusterz.auction_house.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthServiceImp implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    

    @Override
    @Transactional
    public JwtResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw UserException.AlreadyExists.byUsername(request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw UserException.AlreadyExists.byEmail(request.email());
        }

        User newUser = new User();
        newUser.setUsername(request.username());
        newUser.setEmail(request.email());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setUserImageUrl(request.userImageUrl());

        newUser.setRole(UserRole.USER);
        newUser.setActive(true);//to-do set active to false then send email verification
        newUser.setBalance(BigDecimal.ZERO);

        userRepository.save(newUser);

        // create jwt token after registering a new user
        Authentication auth = new UsernamePasswordAuthenticationToken(
            request.username(),
            null,
            List.of(new SimpleGrantedAuthority(UserRole.USER.name())));
        String token = jwtUtils.generateToken(auth);

        return new JwtResponse(token, request.username(), UserRole.USER);
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        User user = userRepository.findByUsernameOrEmail(request.identifier(), request.identifier())
            .orElseThrow(() -> UserException.NotFound.byIdentifier(request.identifier()));

        if (!passwordEncoder.matches(user.getPassword(), request.password())) {
            throw AuthException.Unauthorized.wrongPassword();
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(
            request.identifier(),
            null,
            List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );

        String token = jwtUtils.generateToken(auth);

        return new JwtResponse(token, user.getUsername(), user.getRole());
    }

    @Override
    public void verifyEmail() {}

    @Override
    public void refreshToken() {}
    
}
