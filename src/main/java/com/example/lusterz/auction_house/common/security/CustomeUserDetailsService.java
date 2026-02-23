package com.example.lusterz.auction_house.common.security;

import java.nio.file.attribute.UserPrincipal;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.lusterz.auction_house.common.exception.UserException;
import com.example.lusterz.auction_house.modules.user.repository.UserRepository;

@Service
public class CustomeUserDetailsService implements UserDetailsService{

    private final UserRepository userRepository;

    public CustomeUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(identifier, identifier)
            .orElseThrow UserException.NotFound.byIdentifier();
        
        return UserPrincipal.create(user);
    }
    
}
