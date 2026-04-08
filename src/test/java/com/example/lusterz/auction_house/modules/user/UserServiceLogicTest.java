package com.example.lusterz.auction_house.modules.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

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
import com.example.lusterz.auction_house.modules.auth.model.AuthProviders;
import com.example.lusterz.auction_house.modules.auth.model.VerifyToken;
import com.example.lusterz.auction_house.modules.auth.service.VerifyTokenService;
import com.example.lusterz.auction_house.modules.user.dto.UserUpdateEmailRequest;
import com.example.lusterz.auction_house.modules.user.dto.UserUpdateProfileImageRequest;
import com.example.lusterz.auction_house.modules.user.dto.UserUpdateRoleRequest;
import com.example.lusterz.auction_house.modules.user.dto.UserUpdateUsernameRequest;
import com.example.lusterz.auction_house.modules.user.mapper.UserMapper;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.model.UserRole;
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
        VerifyToken token = new VerifyToken(1L, "VerifyToken", Instant.now(), null); 

        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(verifyTokenService.generateToken(request.email())).thenReturn(token);

        User result = userService.createUser(request);

        assertEquals(request.username(), result.getUsername());
        assertEquals(request.email(), result.getEmail());
        assertEquals(request.userImageUrl(), result.getProfileImage());

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

    @Test
    void activateAccount_PublishVerifyEmailEvent_WhenNotActive() {
        User user = TestData.testUser(1L, false);
        VerifyToken token = new VerifyToken(1L, "VerifyToken", Instant.now(), user);

        when(verifyTokenService.generateToken(user.getEmail())).thenReturn(token);

        // the function must be called 
        userService.activateAccount(user);
        String result = verifyTokenService.generateToken(user.getEmail()).getToken();

        assertNotNull(result);
        verify(applicationEventPublisher).publishEvent(any(VerifyEmailEvent.class));
    }

    @Test
    void activateAccount_ThrowUserExceptionAlreadyActive_WhenActive() {
        User user = TestData.testUser(1L, true);

        assertThrows(UserException.AlreadyActive.class, () -> userService.activateAccount(user));

        verifyNoInteractions(verifyTokenService);
        verifyNoInteractions(applicationEventPublisher);
    }

    @Test
    void deactivateUser_SetActiveToFalse_WhenFound() {
        User user = TestData.testUser(1L, true);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.deactivateUser(user.getId());

        assertFalse(user.isActive());
        verify(userRepository).findById(user.getId());
    }

    @Test
    void deactivateUser_ThrowUserExceptionNotFound_WhenNotFound() {
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserException.NotFound ex = assertThrows(UserException.NotFound.class, () -> userService.deactivateUser(id));
        assertTrue(ex.getMessage().contains(id.toString()));

        verify(userRepository).findById(id);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void processOauth2User_ReturnExistingUser_WhenFound() {
        User user = TestData.testUser(1L, true);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User result = userService.processOauth2User(user.getEmail(), user.getUsername(), AuthProviders.GOOGLE);

        assertEquals(user, result);
        verify(userRepository).findByEmail(user.getEmail());
        verify(userRepository, never()).save(any(User.class));
        verify(userCredentialService, never()).createOauth2UserCredential(any(), any());
    }

    @Test
    void processOauth2User_CreateNewUser_WhenNotFound() {
        User user = TestData.testUser(1L, true);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);

        User result = userService.processOauth2User(user.getEmail(), user.getUsername(), AuthProviders.GOOGLE);

        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getUsername(), result.getUsername());
        assertTrue(result.isActive());

        verify(userRepository).findByEmail(user.getEmail());
        verify(userRepository).existsByUsername(user.getUsername());
        verify(userRepository).save(any(User.class));
        verify(userCredentialService).createOauth2UserCredential(result, AuthProviders.GOOGLE);
    }

    @Test
    void processOauth2User_CreateNewUserAndAppendSuffix_WhenNotFoundAndUsernameNotUnique() {
        User user = TestData.testUser(1L, true);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        User result = userService.processOauth2User(user.getEmail(), user.getUsername(), AuthProviders.GOOGLE);

        assertEquals(user.getEmail(), result.getEmail());
        assertTrue(result.getUsername().contains(user.getUsername()));
        assertTrue(result.isActive());

        verify(userRepository).findByEmail(user.getEmail());
        verify(userRepository).existsByUsername(user.getUsername());
        verify(userRepository).save(any(User.class));
        verify(userCredentialService).createOauth2UserCredential(result, AuthProviders.GOOGLE);
    }

    @Test
    void updateUsername_SetNewUsername_WhenUserFoundAndUsernameUnique() {
        User user = TestData.testUser(1L, true);
        UserUpdateUsernameRequest request = new UserUpdateUsernameRequest(user.getUsername());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);

        String result = userService.updateUsername(user.getId(), request);

        assertEquals(request.username(), result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUsername_ThrowUserExceptionNotFound_WhenUserNotFound() {
        User user = TestData.testUser(1L, true);
        UserUpdateUsernameRequest request = new UserUpdateUsernameRequest(user.getUsername());

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        UserException.NotFound ex = assertThrows(UserException.NotFound.class, () -> userService.updateUsername(user.getId(), request));
        assertTrue(ex.getMessage().contains(user.getId().toString()));
        
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUsername_ThrowUserExceptionAlreadyExists_WhenUsernameExists() {
        User user = TestData.testUser(1L, true);
        UserUpdateUsernameRequest request = new UserUpdateUsernameRequest(user.getUsername());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        UserException.AlreadyExists ex = assertThrows(UserException.AlreadyExists.class, () -> userService.updateUsername(user.getId(), request));
        assertTrue(ex.getMessage().contains(user.getUsername()));
        
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateEmail_SetNewEmail_WhenUserAndEmailExist() {
        User user = TestData.testUser(1L, true);
        UserUpdateEmailRequest request = new UserUpdateEmailRequest(user.getEmail());
        VerifyToken token = new VerifyToken(1L, "VerifyToken", Instant.now(), user);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(verifyTokenService.generateToken(request.email())).thenReturn(token);

        String result = userService.updateEmail(user.getId(), request);

        assertEquals(request.email(), result);
        verify(verifyTokenService).generateToken(request.email());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateEmail_ThrowUserExceptionNotFound_WhenUserNotFound() {
        User user = TestData.testUser(1L, true);
        UserUpdateEmailRequest request = new UserUpdateEmailRequest(user.getEmail());

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        UserException.NotFound ex = assertThrows(UserException.NotFound.class, () -> userService.updateEmail(user.getId(), request));
        assertTrue(ex.getMessage().contains(user.getId().toString()));
        
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(verifyTokenService);
    }

    @Test
    void updateEmail_ThrowUserExceptionAlreadyExists_WhenEmailExists() {
        User user = TestData.testUser(1L, true);
        UserUpdateEmailRequest request = new UserUpdateEmailRequest(user.getEmail());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        UserException.AlreadyExists ex = assertThrows(UserException.AlreadyExists.class, () -> userService.updateEmail(user.getId(), request));
        assertTrue(ex.getMessage().contains(user.getEmail()));
        
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(verifyTokenService);
    }

    @Test
    void updateProfileImage_SetNewImage_WhenUserFound() {
        User user = TestData.testUser(1L, true);
        UserUpdateProfileImageRequest request = new UserUpdateProfileImageRequest(user.getProfileImage());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        String result = userService.updateProfileImage(user.getId(), request);

        assertEquals(request.profileImage(), result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateImageUrl_ThrowUserExceptionNotFound_WhenUserNotFound() {
        User user = TestData.testUser(1L, true);
        UserUpdateProfileImageRequest request = new UserUpdateProfileImageRequest(user.getProfileImage());

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        UserException.NotFound ex = assertThrows(UserException.NotFound.class, () -> userService.updateProfileImage(user.getId(), request));
        assertTrue(ex.getMessage().contains(user.getId().toString()));
        
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateRole_SetNewRole_WhenUserFound() {
        User user = TestData.testUser(1L, true);
        UserUpdateRoleRequest request = new UserUpdateRoleRequest(user.getRole());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserRole result = userService.updateRole(user.getId(), request);

        assertEquals(request.role(), result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateRole_ThrowUserExceptionNotFound_WhenUserNotFound() {
        User user = TestData.testUser(1L, true);
        UserUpdateRoleRequest request = new UserUpdateRoleRequest(user.getRole());

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        UserException.NotFound ex = assertThrows(UserException.NotFound.class, () -> userService.updateRole(user.getId(), request));
        assertTrue(ex.getMessage().contains(user.getId().toString()));
        
        verify(userRepository, never()).save(any(User.class));
    }
}
