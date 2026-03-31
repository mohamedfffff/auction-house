package com.example.lusterz.auction_house.modules.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import com.example.lusterz.auction_house.common.exception.UserException;
import com.example.lusterz.auction_house.infrastructure.notification.dto.VerifyEmailEvent;
import com.example.lusterz.auction_house.modules.auth.dto.RegisterRequest;
import com.example.lusterz.auction_house.modules.auth.model.VerifyToken;
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
    void createUser_ReturnUser_WhenValidRequest() {
        RegisterRequest request = TestData.testRegisterRequest();
        VerifyToken token = TestData.testVerifyToken(); 

        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(verifyTokenService.generateToken(request.email())).thenReturn(token);

        User result = userService.createUser(request);

        assertEquals(request.username(), result.getUsername());
        assertEquals(request.email(), result.getEmail());
        assertEquals(request.userImageUrl(), result.getUserImageUrl());

        verify(userRepository).save(any(User.class));
        // any(User.class) don't work cause mockito requires all args to be matters
        verify(userCredentialService).createLocalUserCredential(request, result);
        verify(verifyTokenService).generateToken(request.email());
        // anyObject() do not exists
        verify(applicationEventPublisher).publishEvent(any(VerifyEmailEvent.class));
    }

    @Test
    void createUser_ThrowUserExceptionAlreadyExists_WhenUsernameExists() {
        RegisterRequest request = TestData.testRegisterRequest();

        when(userRepository.existsByUsername(request.username())).thenReturn(true);
        
        UserException.AlreadyExists ex = assertThrows(UserException.AlreadyExists.class, () -> userService.createUser(request));
        assertTrue(ex.getMessage().contains(request.username()));

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(userCredentialService);
        verifyNoInteractions(verifyTokenService);
        verifyNoInteractions(applicationEventPublisher);
    }

    @Test
    void createUser_ThrowUserExceptionAlreadyExists_WhenEmailExists() {
        RegisterRequest request = TestData.testRegisterRequest();

        when(userRepository.existsByEmail(request.email())).thenReturn(true);
        
        UserException.AlreadyExists ex = assertThrows(UserException.AlreadyExists.class, () -> userService.createUser(request));
        assertTrue(ex.getMessage().contains(request.email()));

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(userCredentialService);
        verifyNoInteractions(verifyTokenService);
        verifyNoInteractions(applicationEventPublisher);
    }
}
