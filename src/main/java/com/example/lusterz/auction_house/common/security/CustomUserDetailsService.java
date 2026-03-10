package com.example.lusterz.auction_house.common.security;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.lusterz.auction_house.common.exception.UserException;
import com.example.lusterz.auction_house.modules.auth.model.AuthProviders;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.model.UserCredential;
import com.example.lusterz.auction_house.modules.user.repository.UserCredentialRepository;
import com.example.lusterz.auction_house.modules.user.repository.UserRepository;
import com.example.lusterz.auction_house.modules.user.service.UserCredentialService;
import com.example.lusterz.auction_house.modules.user.service.UserService;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService{

    private final UserService userService;
    private final UserCredentialService userCredentialService;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        
        User user = userService.getByUsernameOrEmail(identifier);

        UserCredential userCredential = userCredentialService.getByUserAndProvider(user, AuthProviders.LOCAL);
        
        List<SimpleGrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority(user.getRole().name())
        );

        // return User Details not User entity
        // detailed class path is used to differ it from project User entity
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            userCredential.getPassword(),
            authorities
        );
    }
    
}
