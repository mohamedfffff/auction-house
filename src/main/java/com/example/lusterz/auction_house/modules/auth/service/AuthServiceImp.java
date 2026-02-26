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
import com.example.lusterz.auction_house.modules.auth.dto.AuthResponse;
import com.example.lusterz.auction_house.modules.auth.dto.AuthUserDto;
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
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw UserException.AlreadyExists.byUsername(request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw UserException.AlreadyExists.byEmail(request.email());
        }

        User newUser = User.builder()
            .username(request.username())
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .userImageUrl(request.userImageUrl())
            .role(UserRole.USER)
            .active(true)//to-do set active to false then send email verification
            .balance(BigDecimal.ZERO)
            .build();

        userRepository.save(newUser);

        // create jwt token after registering a new user
        Authentication auth = new UsernamePasswordAuthenticationToken(
            newUser,
            null,   
            List.of(new SimpleGrantedAuthority(UserRole.USER.name())));
        String token = jwtUtils.generateToken(auth);

        return new AuthResponse(
            token,
            "Bearer",
            jwtUtils.getJwtExpiration(),
            new AuthUserDto(newUser.getUsername(), newUser.getRole())
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsernameOrEmail(request.identifier(), request.identifier())
            .orElseThrow(() -> UserException.NotFound.byIdentifier(request.identifier()));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw AuthException.Unauthorized.wrongPassword();
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(
            user,
            null,
            List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );

        String token = jwtUtils.generateToken(auth);

        return new AuthResponse(
            token,
            "Bearer",
            jwtUtils.getJwtExpiration(),
            new AuthUserDto(user.getUsername(), user.getRole())
        );
    }

    @Override
    public void verifyEmail() {}

    @Override
    public void refreshToken() {}
    
}
