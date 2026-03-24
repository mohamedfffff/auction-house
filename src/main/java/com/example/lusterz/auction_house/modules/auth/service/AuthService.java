package com.example.lusterz.auction_house.modules.auth.service;


import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lusterz.auction_house.common.exception.AuthException;
import com.example.lusterz.auction_house.common.util.JwtUtils;
import com.example.lusterz.auction_house.infrastructure.dto.ResetPasswordEvent;
import com.example.lusterz.auction_house.modules.auth.dto.AuthResponse;
import com.example.lusterz.auction_house.modules.auth.dto.LoginRequest;
import com.example.lusterz.auction_house.modules.auth.dto.RefreshTokenRequest;
import com.example.lusterz.auction_house.modules.auth.dto.RegisterRequest;
import com.example.lusterz.auction_house.modules.auth.dto.ResetPasswordRequest;
import com.example.lusterz.auction_house.modules.auth.model.AuthProviders;
import com.example.lusterz.auction_house.modules.auth.model.RefreshToken;
import com.example.lusterz.auction_house.modules.auth.model.ResetPasswordToken;
import com.example.lusterz.auction_house.modules.auth.model.VerifyToken;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.service.UserCredentialService;
import com.example.lusterz.auction_house.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private final UserService userService;
    private final UserCredentialService userCredentialService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final VerifyTokenService verifyTokenService;
    private final ResetPasswordTokenService resetPasswordTokenService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        
        User user = userService.createUser(request);
        log.info("User : {} registered using form", user.getUsername());
        
        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // no need to check if password is correct cause AuthenticationManager handles it
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.identifier(), request.password())
        );

        User user = userService.getByUsernameOrEmail(request.identifier());

        log.info("User : {} logged-in using form", user.getUsername());

        return generateAuthResponse(user);
    }

    @Transactional
    public void verifyEmail(String token) {
        VerifyToken verifyToken = verifyTokenService.getByToken(token);
        User user = verifyToken.getUser();

        if (verifyTokenService.expired(verifyToken)) {
            throw AuthException.VerifyToken.expired();
        }

        user.setActive(true);

        verifyTokenService.deleteUsedToken(token);

        log.info("Verified user : {}", user.getUsername());
    }

    // needs more validation logic
    @Transactional
    public void forgotPassword(String email) {

        String token = resetPasswordTokenService.generateToken(email).getToken();
        
        eventPublisher.publishEvent(
            new ResetPasswordEvent(email, token)
        );
    } 

    @Transactional
    public void setPassword() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = (UserDetails) auth.getPrincipal();
        String username = principal.getUsername();

        // access token returns user details and it only contains the username
        // db call is not expensive here instead of changing the whole security 
        // sturcture to use email instead of username which isn't super safe
        User user = userService.getByUsernameOrEmail(username);

        String token = resetPasswordTokenService.generateToken(user.getEmail()).getToken();
        
        eventPublisher.publishEvent(
            new ResetPasswordEvent(user.getEmail(), token)
        );
    }

    @Transactional
    public AuthResponse resetPassword(ResetPasswordRequest request) {
        ResetPasswordToken resetToken = resetPasswordTokenService.getByToken(request.token());
        User user = resetToken.getUser();

        if (resetPasswordTokenService.expired(resetToken)) {
            throw AuthException.ResetPasswordToken.expired();
        }

        // reset password for local users and create new local user creadential for oauth2
        if (AuthProviders.LOCAL.equals(request.provider())) {
            userService.resetPassword(user.getId(), request.password());
        } else { userCredentialService.createLocalUserCredential(user, request.password());}
    
        resetPasswordTokenService.deleteUsedToken(resetToken.getToken());

        log.info("Password reset for user : {}", user.getUsername());

        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse refreshAccessToken(RefreshTokenRequest request) {
        RefreshToken oldRefreshToken = refreshTokenService.getRefreshToken(request.token());

        if (refreshTokenService.expired(oldRefreshToken)) {
            throw AuthException.RefreshToken.expired();
        }

        User user = oldRefreshToken.getUser();

        log.info("Token refreshed for user : {}", user.getUsername());

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
