package com.example.lusterz.auction_house.modules.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.example.lusterz.auction_house.TestData;
import com.example.lusterz.auction_house.infrastructure.notification.dto.VerifyEmailEvent;
import com.example.lusterz.auction_house.modules.auth.dto.RegisterRequest;
import com.example.lusterz.auction_house.modules.auth.model.AuthProviders;
import com.example.lusterz.auction_house.modules.auth.model.VerifyToken;
import com.example.lusterz.auction_house.modules.auth.repository.VerifyTokenRepository;
import com.example.lusterz.auction_house.modules.auth.service.VerifyTokenService;
import com.example.lusterz.auction_house.modules.user.mapper.UserMapper;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.repository.UserRepository;
import com.example.lusterz.auction_house.modules.user.service.UserCredentialService;
import com.example.lusterz.auction_house.modules.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceLogicTest {
    
    @Mock private UserRepository userRepository;
    @Mock private UserCredentialService userCredentialService;
    @Mock private VerifyTokenService verifyTokenService;
    @Mock private ApplicationEventPublisher applicationEventPublisher;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private UserService userService;


    @Test
    void createUser_ReturnUser_WhenNotFound() {
        RegisterRequest request = TestData.testRegisterRequest();
        VerifyToken token = TestData.testVerifyToken(); 

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(verifyTokenService.generateToken(anyString())).thenReturn(token);

        User result = userService.createUser(request);

        assertEquals(request.username(), result.getUsername());
        assertEquals(request.email(), result.getEmail());

        verify(userRepository).save(any(User.class));
        // any(User.class) don't work cause mockito requires all args to be matters
        verify(userCredentialService).createLocalUserCredential(request, result);
        verify(verifyTokenService).generateToken(anyString());
        // anyObject() do not exists
        verify(applicationEventPublisher).publishEvent(any(VerifyEmailEvent.class));
    }
}
