package com.example.lusterz.auction_house.modules.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.example.lusterz.auction_house.TestData;
import com.example.lusterz.auction_house.modules.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.lusterz.auction_house.common.exception.UserException;
import com.example.lusterz.auction_house.modules.user.dto.UserPrivateDto;
import com.example.lusterz.auction_house.modules.user.dto.UserPublicDto;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.repository.UserRepository;
import com.example.lusterz.auction_house.modules.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceQueryTest {
    
    @Mock
    private UserRepository userRepository;

    @Spy// it is fine to use spy since it is a simple mapper
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
 
    @InjectMocks
    private UserService userService;

    @Test
    void getUserById_ReturnPrivateDto_WhenFound() {
        Long id = 1L;
        User user = TestData.testUser(id, true);
        UserPrivateDto dto = TestData.testUserPrivateDto(user);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

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
        UserPublicDto dto = TestData.testUserPublicDto(user);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

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
    void getAllActiveUsers_ReturnDtoList_WhenFound() {
        User active1 = TestData.testUser(1L, true);
        User active2 = TestData.testUser(2L, true);
        List<User> userList = List.of(active1, active2);
        UserPrivateDto active1Dto = TestData.testUserPrivateDto(active1);
        UserPrivateDto active2Dto = TestData.testUserPrivateDto(active2);

        when(userRepository.findAllByActive(true)).thenReturn(userList);

        List<UserPrivateDto> result = userService.getAllActiveUsers();

        assertEquals(2, result.size());
        assertEquals(active1Dto, result.get(0));
        assertEquals(active2Dto, result.get(1));

        verify(userRepository).findAllByActive(true);
        verify(userMapper, times(2)).toPrivateDto(any(User.class));
    }

    @Test
    void getAllActiveUsers_ReturnEmptyList_WhenNotFound() {

        when(userRepository.findAllByActive(true)).thenReturn(List.of());

        List<UserPrivateDto> result = userService.getAllActiveUsers();

        assertTrue(result.isEmpty());
        verify(userRepository).findAllByActive(true);
        verifyNoInteractions(userMapper);
    }

    @Test
    void getAllUnactiveUsers_ReturnDtoList_WhenFound() {
        User unactive1 = TestData.testUser(1L, false);
        User unactive2 = TestData.testUser(2L, false);
        List<User> userList = List.of(unactive1, unactive2);
        UserPrivateDto unactive1Dto = TestData.testUserPrivateDto(unactive1);
        UserPrivateDto unactive2Dto = TestData.testUserPrivateDto(unactive2);

        when(userRepository.findAllByActive(false)).thenReturn(userList);

        List<UserPrivateDto> result = userService.getAllUnactiveUsers();

        assertEquals(2, result.size());
        assertEquals(unactive1Dto, result.get(0));
        assertEquals(unactive2Dto, result.get(1));

        verify(userRepository).findAllByActive(false);
        verify(userMapper, times(2)).toPrivateDto(any(User.class));
    }

    @Test
    void getAllUnactiveUsers_ReturnEmptyList_WhenNotFound() {

        when(userRepository.findAllByActive(false)).thenReturn(List.of());

        List<UserPrivateDto> result = userService.getAllUnactiveUsers();

        assertTrue(result.isEmpty());
        verify(userRepository).findAllByActive(false);
        verifyNoInteractions(userMapper);
    }

    @Test
    void getAllUsers_ReturnDtoList_WhenFound() {
        User active = TestData.testUser(1L, true);
        User unactive = TestData.testUser(2L, false);
        List<User> userList = List.of(active, unactive);
        UserPrivateDto activeDto = TestData.testUserPrivateDto(active);
        UserPrivateDto unactiveDto = TestData.testUserPrivateDto(unactive);

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
