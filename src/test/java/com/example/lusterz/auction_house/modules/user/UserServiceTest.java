package com.example.lusterz.auction_house.modules.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.lusterz.auction_house.TestData;
import com.example.lusterz.auction_house.modules.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.lusterz.auction_house.common.exception.UserException;
import com.example.lusterz.auction_house.modules.user.dto.UserPrivateDto;
import com.example.lusterz.auction_house.modules.user.dto.UserPublicDto;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.repository.UserRepository;
import com.example.lusterz.auction_house.modules.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserById_ReturnPrivateDto_WhenFound() {
        Long id = 1L;
        User user = TestData.testUser(id, true);
        UserPrivateDto dto = new UserPrivateDto();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toPrivateDto(user)).thenReturn(dto);

        UserPrivateDto result = userService.getUserById(id);

        assertEquals(dto, result);
        verify(userRepository).findById(id);
        verify(userMapper).toPrivateDto(user);
    }

    @Test
    void getUserById_ThrowUserExceptionNotFound_WhenNotFound() {
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserException.NotFound ex = assertThrows(UserException.NotFound.class, () -> userService.getUserById(id));

        assertTrue(ex.getMessage().contains(id.toString()));
        verify(userRepository).findById(id);
        verifyNoInteractions(userMapper);
    }

    @Test
    void getUserByName_ReturnPublicDto_WhenFound() {
        User user = TestData.testUser(1L, true);
        String username = user.getUsername();
        UserPublicDto dto = new UserPublicDto();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userMapper.toPublicDto(user)).thenReturn(dto);

        UserPublicDto result = userService.getUserByName(username);

        assertEquals(dto, result);
        verify(userRepository).findByUsername(username);
        verify(userMapper).toPublicDto(user);
    }

    @Test
    void getUserByName_ThrowUserExceptionNotFound_WhenNotFound() {
        String username = "notUsername";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UserException.NotFound ex = assertThrows(UserException.NotFound.class, () -> userService.getUserByName(username));
        
        assertTrue(ex.getMessage().contains(username));
        verify(userRepository).findByUsername(username);
        verifyNoInteractions(userMapper);
    }

    @Test
    void getUserByEmail_ReturnUser_WhenFound() {
        User user = TestData.testUser(1L, true);
        String email = user.getEmail();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.getUserByEmail(email);

        assertEquals(user, result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void getUserByEmail_ThrowUserExceptionNotFound_WhenNotFound() {
        String email = "notEmail";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UserException.NotFound ex = assertThrows(UserException.NotFound.class, () -> userService.getUserByEmail(email));
        
        assertTrue(ex.getMessage().contains(email));
        verify(userRepository).findByEmail(email);
    }

    @Test
    void getUserByIdentifier_ReturnUser_WhenEmailProvided() {
        User user = TestData.testUser(1L, true);
        String identifier = user.getEmail();

        when(userRepository.findByUsernameOrEmail(identifier, identifier)).thenReturn(Optional.of(user));

        User result = userService.getUserByIdentifier(identifier);

        assertEquals(user, result);
        verify(userRepository).findByUsernameOrEmail(identifier, identifier);
    }

    @Test
    void getUserByIdentifier_ReturnUser_WhenUsernameProvided() {
        User user = TestData.testUser(1L, true);
        String identifier = user.getUsername();

        when(userRepository.findByUsernameOrEmail(identifier, identifier)).thenReturn(Optional.of(user));

        User result = userService.getUserByIdentifier(identifier);

        assertEquals(user, result);
        verify(userRepository).findByUsernameOrEmail(identifier, identifier);
    }

    @Test
    void getUserByIdentifier_ThrowUserExceptionNotFound_WhenNotFound() {
        String identifier = "notIdentifier";

        when(userRepository.findByUsernameOrEmail(identifier, identifier)).thenReturn(Optional.empty());

        UserException.NotFound ex = assertThrows(UserException.NotFound.class, () -> userService.getUserByIdentifier(identifier));
        
        assertTrue(ex.getMessage().contains(identifier));
        verify(userRepository).findByUsernameOrEmail(identifier, identifier);
    }

    @Test
    void existsByEmail_ReturnTrue_WhenFound() {
        String email = TestData.testUser(1L, true).getEmail();

        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean result = userService.existsByEmail(email);

        assertTrue(result);
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void existsByEmail_ReturnFalse_WhenNotFound() {
        String email = TestData.testUser(1L, true).getEmail();

        when(userRepository.existsByEmail(email)).thenReturn(false);

        boolean result = userService.existsByEmail(email);

        assertFalse(result);
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void getAllUsers_ReturnDtoList_WhenFound() {
        User active = TestData.testUser(1L, true);
        User unactive = TestData.testUser(2L, false);
        List<User> userList = List.of(active, unactive);
        UserPrivateDto activeDto = new UserPrivateDto();
        UserPrivateDto unactiveDto = new UserPrivateDto();

        when(userRepository.findAll()).thenReturn(userList);

        List<UserPrivateDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(activeDto, result.get(0));
        assertEquals(unactiveDto, result.get(1));

        verify(userRepository).findAll();
        verify(userMapper, times(2)).toPrivateDto(any(User.class));
    }

    @Test
    void getAllUsers_ReturnEmptyList_WhenNotFound() {

        when(userRepository.findAll()).thenReturn(List.of());

        List<UserPrivateDto> result = userService.getAllUsers();

        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
        verifyNoInteractions(userMapper);
    }
}
