package com.example.lusterz.auction_house.modules.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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

    @InjectMocks
    private UserService userService;

    private User user;
    private User activeUser;
    private User unactiveUser;
    
    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setEmail("userEmail@gmail.com");
    }

    @Test
    void getUserById_ShouldReturnUserPrivateDto_WhenUserExists() {
        Long id = user.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserPrivateDto result = userService.getUserById(id);

        assertEquals(result.username(), user.getUsername());
    }

    @Test
    void getUserById_ShouldThrowUserExceptionNotFound_WhenUserDoesNotExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserException.NotFound.class, () -> userService.getUserById(1L));
    }

    @Test
    void getUserByName_ShouldReturnUserPublicDto_WhenUserExists() {
        String username = user.getUsername();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserPublicDto result = userService.getUserByName(username);

        assertEquals(result.username(), user.getUsername());
    }

    @Test
    void getUserByName_ShouldThrowUserExceptionNotFound_WhenUserDoesNotExist() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UserException.NotFound.class, () -> userService.getUserByName("notUser"));
    }
}
