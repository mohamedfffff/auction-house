package com.example.lusterz.auction_house.modules.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.lusterz.auction_house.TestData;
import com.example.lusterz.auction_house.modules.auth.dto.RegisterRequest;
import com.example.lusterz.auction_house.modules.auth.model.AuthProviders;
import com.example.lusterz.auction_house.modules.auth.repository.VerifyTokenRepository;
import com.example.lusterz.auction_house.modules.auth.service.VerifyTokenService;
import com.example.lusterz.auction_house.modules.user.mapper.UserMapper;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.repository.UserRepository;
import com.example.lusterz.auction_house.modules.user.service.UserCredentialService;
import com.example.lusterz.auction_house.modules.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceLogicTest {
    
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserCredentialService userCredentialService;
    @Mock
    private VerifyTokenService verifyTokenService;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_ReturnUser_WhenNotFound() {
        User user = TestData.testUser(1L, false);
        RegisterRequest request = new RegisterRequest(
            user.getUsername(),
            user.getEmail(),
            "userPassword",
            user.getUserImageUrl(),
            AuthProviders.LOCAL);

        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        // passing any user entity as local User doesn't match the created enity in method
        // then return the same exact entity created in method
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.createUser(request);

        assertEquals(request.username(), result.getUsername());
        assertEquals(request.email(), result.getEmail());

        verify(userRepository).save(any(User.class));
        verify(userCredentialService).createLocalUserCredential(request, any(User.class));
        verify(verifyTokenService).generateToken(result.getEmail());
    }
}
