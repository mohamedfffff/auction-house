package com.example.lusterz.auction_house.common.security;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.model.UserRole;
import com.example.lusterz.auction_house.modules.user.service.UserCredentialService;
import com.example.lusterz.auction_house.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService{

    private final UserService userService;
    private final UserCredentialService userCredentialService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        String email = user.getAttribute("email");
        String name = user.getAttribute("name");
        String providerId = userRequest.getClientRegistration().getRegistrationId();

        processNewUser(email, name, providerId);

        return user;
    }

    private void processNewUser(String email, String name, String providerId) {
        if (!userService.existsByEmail(email)) {
            User newUser = User.builder()
                .username(generateUniqueUsername(name))
                .email(email)
                .role(UserRole.USER)
                .active(true)//to-do set active to false then send email verification
                .balance(BigDecimal.ZERO)
                .build();
        }
    }

    private String generateUniqueUsername(String name) {
        return "";
    }
    
}
