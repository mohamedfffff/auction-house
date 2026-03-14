package com.example.lusterz.auction_house.common.security;


import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import com.example.lusterz.auction_house.common.exception.AuthException;
import com.example.lusterz.auction_house.modules.auth.model.AuthProviders;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomOauth2UserService extends DefaultOAuth2UserService{

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // check if email isn't provided by provider
        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            throw AuthException.Provider.noEmail();
        }
        String name = oAuth2User.getAttribute("name");
        AuthProviders provider = AuthProviders.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        String usernameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        User user = userService.processOauth2User(email, name, provider);

        List<SimpleGrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority(user.getRole().name())
        );

        return new DefaultOAuth2User(
            authorities, 
            oAuth2User.getAttributes(), 
            usernameAttributeName);
    }
    
}
