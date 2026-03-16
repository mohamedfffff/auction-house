package com.example.lusterz.auction_house.modules.auth.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lusterz.auction_house.common.exception.AuthException;
import com.example.lusterz.auction_house.common.util.JwtUtils;
import com.example.lusterz.auction_house.modules.auth.dto.AuthResponse;
import com.example.lusterz.auction_house.modules.auth.dto.LoginRequest;
import com.example.lusterz.auction_house.modules.auth.dto.RefreshTokenRequest;
import com.example.lusterz.auction_house.modules.auth.dto.RegisterRequest;
import com.example.lusterz.auction_house.modules.auth.dto.VerifyRequest;
import com.example.lusterz.auction_house.modules.auth.model.RefreshToken;
import com.example.lusterz.auction_house.modules.auth.model.VerifyToken;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final VerifyTokenService verifyTokenService;
    

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        
        User user = userService.createUser(request);
        log.info("User {} registered using form", user.getUsername());
        
        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // no need to check if password is correct cause AuthenticationManager handles it
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.identifier(), request.password())
        );

        User user = userService.getByUsernameOrEmail(request.identifier());

        log.info("User {} logged-in using form", user.getUsername());

        return generateAuthResponse(user);
    }

    @Transactional
    public void verifyEmail(VerifyRequest request) {
        VerifyToken token = verifyTokenService.getByToken(request.token());
        User user = token.getUser();

        if (verifyTokenService.expired(token)) {
            throw AuthException.VerifyToken.expired();
        }

        user.setActive(true);

        log.info("Verified user {}", user.getUsername());
    }

    @Transactional
    public AuthResponse refreshAccessToken(RefreshTokenRequest request) {
        RefreshToken oldRefreshToken = refreshTokenService.getRefreshToken(request.refreshToken());

        if (refreshTokenService.expired(oldRefreshToken)) {
            throw AuthException.RefreshToken.expired();
        }

        User user = oldRefreshToken.getUser();

        log.info("Token refreshed for user {}", user.getUsername());

        return generateAuthResponse(user);
    }

    private AuthResponse generateAuthResponse(User user) {
        String newAccessToken = jwtUtils.generateToken(user.getUsername());
        String newRefreshToken = refreshTokenService.generateToken(user).getToken();

        return new AuthResponse(
            newAccessToken,
            newRefreshToken,
            "Bearer",
            jwtUtils.getJwtExpiration(),
            user.getUsername(),
            user.getRole().name()
        );
    }

    

    
}
