package com.example.lusterz.auction_house.modules.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
        User user = TestData.testUser();
        UserPrivateDto dto = TestData.testUserPrivateDto();

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

        assertThrows(UserException.NotFound.class, () -> userService.getUserById(id));
        verify(userRepository).findById(id);
        verifyNoInteractions(userMapper);
    }

    @Test
    void getUserByName_ReturnPublicDto_WhenFound() {
        User user = TestData.testUser();
        String username = user.getUsername();
        UserPublicDto dto = TestData.testUserPublicDto();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userMapper.toPublicDto(user)).thenReturn(dto);

        UserPublicDto result = userService.getUserByName(username);

        assertEquals(dto, result);
        verify(userRepository).findByUsername(username);
        verify(userMapper).toPublicDto(user);
    }

    @Test
    void getUserByName_ThrowUserExceptionNotFound_WhenNotFound() {
        String username = "notUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserException.NotFound.class, () -> userService.getUserByName(username));
        verify(userRepository).findByUsername(username);
        verifyNoInteractions(userMapper);
    }
}
