package com.example.lusterz.auction_house.common.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.lusterz.auction_house.common.util.JwtUtils;
import com.example.lusterz.auction_house.modules.auth.service.RefreshTokenService;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class Oauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler{

    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication auth) throws IOException {
        
        
        OAuth2User oAuth2User = (OAuth2User) auth.getPrincipal(); 
        String email = oAuth2User.getAttribute("email");

        User user = userService.getUserByEmail(email);   

        // oAuth2User.getAttribute("name") gives a code not user username stored in memory
        String accessToken = jwtUtils.generateToken(user.getUsername());
        String refreshToken = refreshTokenService.generateToken(user.getId()).getToken() ;
        Long expiration = jwtUtils.getJwtExpiration();

        String tergetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2-callback")
            .queryParam("accessToken", accessToken)
            .queryParam("refreshToken", refreshToken)
            .queryParam("type", "Bearer")
            .queryParam("expiration", expiration)
            .queryParam("username", user.getUsername())
            .queryParam("role", user.getRole())
            .build().toUriString();
        
        getRedirectStrategy().sendRedirect(request, response, tergetUrl);

        log.info("User {} logged-in using oauth2", user.getUsername());
    }
}
