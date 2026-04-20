package com.example.lusterz.auction_house.unit.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.lusterz.auction_house.TestData;
import com.example.lusterz.auction_house.common.exception.UserException;
import com.example.lusterz.auction_house.modules.auth.dto.RegisterRequest;
import com.example.lusterz.auction_house.modules.auth.model.AuthProviders;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.model.UserCredential;
import com.example.lusterz.auction_house.modules.user.repository.UserCredentialRepository;
import com.example.lusterz.auction_house.modules.user.service.UserCredentialService;

@ExtendWith(MockitoExtension.class)
public class UserCredentialServiceTest {
    
    @Mock private UserCredentialRepository userCredentialRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserCredentialService userCredentialService;

    @Test
    void getByUserAndProvider_ShouldReturnUserCredentialOptional_WhenFound() {
        User user = new User(); 
        UserCredential userCredential = new UserCredential();
        AuthProviders provider = AuthProviders.LOCAL;

        when(userCredentialRepository.findByUserAndProvider(user, provider)).thenReturn(Optional.of(userCredential));
    
        Optional<UserCredential> result = userCredentialService.getByUserAndProvider(user, provider);

        assertTrue(result.isPresent());
        verify(userCredentialRepository).findByUserAndProvider(any(User.class), any(AuthProviders.class));
    }

    @Test
    void getByUserAndProvider_ShouldReturnEmptyOptional_WhenNotFound() {
        User user = new User(); 
        AuthProviders provider = AuthProviders.LOCAL;

        when(userCredentialRepository.findByUserAndProvider(user, provider)).thenReturn(Optional.empty());
    
        Optional<UserCredential> result = userCredentialService.getByUserAndProvider(user, provider);

        assertTrue(result.isEmpty());
        verify(userCredentialRepository).findByUserAndProvider(any(User.class), any(AuthProviders.class));
    }

    @Test
    void createLocalUserCredential_ShouldSaveLocalCredential_WhenNotFound_WithUserAndRequest() {
        User user = TestData.testUser(1L, false);
        RegisterRequest request = TestData.testRegisterRequest();
        String hashedPassword = "hashed";

        when(userCredentialRepository.existsByUserAndProvider(user, request.provider())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn(hashedPassword);

        UserCredential result = userCredentialService.createLocalUserCredential(request, user);

        assertEquals(user, result.getUser());
        assertEquals(request.provider(), result.getProvider());
        assertEquals(hashedPassword, result.getPassword());

        verify(userCredentialRepository).existsByUserAndProvider(user, request.provider());
        verify(passwordEncoder).encode(request.password());
        verify(userCredentialRepository).save(any(UserCredential.class));
    }

    @Test
    void createLocalUserCredential_ThrowUserExceptionCredential_WhenFound_WithUserAndRequest() {
        User user = TestData.testUser(1L, false);
        RegisterRequest request = TestData.testRegisterRequest();

        when(userCredentialRepository.existsByUserAndProvider(user, request.provider())).thenReturn(true);

        assertThrows(UserException.Credential.class, () -> userCredentialService.createLocalUserCredential(request, user));

        verify(userCredentialRepository).existsByUserAndProvider(user, request.provider());
        verify(userCredentialRepository, times(0)).save(any(UserCredential.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void createLocalUserCredential_ShouldSaveLocalCredential_WhenNotFound_WithPasswordAndUser() {
        User user = TestData.testUser(1L, false);
        AuthProviders provider = AuthProviders.LOCAL;
        String oldPassword = "rawPassword";
        String hashedPassword = "hashed";

        when(userCredentialRepository.existsByUserAndProvider(user, AuthProviders.LOCAL)).thenReturn(false);
        when(passwordEncoder.encode(oldPassword)).thenReturn(hashedPassword);

        UserCredential result = userCredentialService.createLocalUserCredential(user, oldPassword);

        assertEquals(user, result.getUser());
        assertEquals(provider, result.getProvider());
        assertEquals(hashedPassword, result.getPassword());

        verify(userCredentialRepository).existsByUserAndProvider(user, provider);
        verify(passwordEncoder).encode(oldPassword);
        verify(userCredentialRepository).save(any(UserCredential.class));
    }

    @Test
    void createLocalUserCredential_ThrowUserExceptionCredential_WhenFound_WithPasswordAndUser() {
        User user = TestData.testUser(1L, false);
        AuthProviders provider = AuthProviders.LOCAL;
        String oldPassword = "rawPassword";

        when(userCredentialRepository.existsByUserAndProvider(user, provider)).thenReturn(true);

        assertThrows(UserException.Credential.class, () -> userCredentialService.createLocalUserCredential(user, oldPassword));

        verify(userCredentialRepository).existsByUserAndProvider(user, provider);
        verify(userCredentialRepository, times(0)).save(any(UserCredential.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void createOauth2UserCredential_ShouldSaveLocalCredential_WhenNotFound() {
        User user = TestData.testUser(1L, false);
        AuthProviders provider = AuthProviders.GOOGLE;

        when(userCredentialRepository.existsByUserAndProvider(user, AuthProviders.GOOGLE)).thenReturn(false);

        UserCredential result = userCredentialService.createOauth2UserCredential(user, provider);

        assertEquals(user, result.getUser());
        assertEquals(provider, result.getProvider());

        verify(userCredentialRepository).existsByUserAndProvider(user, provider);
        verify(userCredentialRepository).save(any(UserCredential.class));
    }

    @Test
    void createOauth2UserCredential_ThrowUserExceptionCredential_WhenFound() {
        User user = TestData.testUser(1L, false);
        AuthProviders provider = AuthProviders.GOOGLE;

        when(userCredentialRepository.existsByUserAndProvider(user, provider)).thenReturn(true);

        assertThrows(UserException.Credential.class, () -> userCredentialService.createOauth2UserCredential(user, provider));

        verify(userCredentialRepository).existsByUserAndProvider(user, provider);
        verify(userCredentialRepository, times(0)).save(any(UserCredential.class));
    }
}
