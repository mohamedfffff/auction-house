package com.example.lusterz.auction_house.modules.auth.service;

import java.math.BigDecimal;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lusterz.auction_house.common.exception.AuthException;
import com.example.lusterz.auction_house.common.exception.UserException;
import com.example.lusterz.auction_house.common.util.JwtUtils;
import com.example.lusterz.auction_house.modules.auth.dto.AuthResponse;
import com.example.lusterz.auction_house.modules.auth.dto.AuthUserDto;
import com.example.lusterz.auction_house.modules.auth.dto.LoginRequest;
import com.example.lusterz.auction_house.modules.auth.dto.RefreshTokenRequest;
import com.example.lusterz.auction_house.modules.auth.dto.RegisterRequest;
import com.example.lusterz.auction_house.modules.auth.model.RefreshToken;
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
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    

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
        
        return generateAuthResponse(newUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // no need to check if user exist or password is correct cause AuthenticationManager handles it
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.identifier(), request.password())
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        User user = (User) auth.getPrincipal();

        return generateAuthResponse(user);
    }

    @Override
    public AuthResponse refreshAccessToken(RefreshTokenRequest request) {
        RefreshToken oldRefreshToken = refreshTokenService.getRefreshToken(request.refreshToken());

        if (refreshTokenService.expired(oldRefreshToken)) {
            throw AuthException.RefreshToken.expired();
        }

        User user = oldRefreshToken.getUser();

        return generateAuthResponse(user);
    }

    private AuthResponse generateAuthResponse(User user) {
        String newAccessToken = jwtUtils.generateTokenFromUsername(user.getUsername());
        String newRefreshToken = refreshTokenService.generateToken(user.getId()).getToken();

        return new AuthResponse(
            newAccessToken,
            newRefreshToken,
            "Bearer",
            jwtUtils.getJwtExpiration(),
            new AuthUserDto(user.getUsername(), user.getRole().name())
        );
    }

    // to-do
    // @Override
    // public void verifyEmail() {}

    
}
