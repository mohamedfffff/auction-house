package com.example.lusterz.auction_house.modules.user.service;

import com.example.lusterz.auction_house.modules.auth.service.VerifyTokenService;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lusterz.auction_house.common.exception.AuthException;
import com.example.lusterz.auction_house.common.exception.UserException;
import com.example.lusterz.auction_house.infrastructure.notification.dto.VerifyEmailEvent;
import com.example.lusterz.auction_house.modules.auth.dto.RegisterRequest;
import com.example.lusterz.auction_house.modules.auth.model.AuthProviders;
import com.example.lusterz.auction_house.modules.user.dto.UserPrivateDto;
import com.example.lusterz.auction_house.modules.user.dto.UserPublicDto;
import com.example.lusterz.auction_house.modules.user.dto.UserUpdateEmailRequest;
import com.example.lusterz.auction_house.modules.user.dto.UserUpdateImageUrlRequest;
import com.example.lusterz.auction_house.modules.user.dto.UserUpdatePasswordRequest;
import com.example.lusterz.auction_house.modules.user.dto.UserUpdateRoleRequest;
import com.example.lusterz.auction_house.modules.user.dto.UserUpdateUsernameRequest;
import com.example.lusterz.auction_house.modules.user.mapper.UserMapper;
import com.example.lusterz.auction_house.modules.user.model.User;
import com.example.lusterz.auction_house.modules.user.model.UserCredential;
import com.example.lusterz.auction_house.modules.user.repository.UserRepository;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class UserService {

    private final VerifyTokenService verifyTokenService;
    private final UserRepository userRepository;
    private final UserCredentialService userCredentialService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
 
    public UserPrivateDto getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> UserException.NotFound.byId(id));
        return userMapper.toPrivateDto(user);
    }

    public UserPublicDto getUserByName(String username) {
        User user = userRepository.findByUsername(username)
        .orElseThrow(() -> UserException.NotFound.byUsername(username));
        return userMapper.toPublicDto(user); 
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> UserException.NotFound.byEmail(email));
    }
    
    public User getUserByIdentifier(String identifier) {
        User user = userRepository.findByUsernameOrEmail(identifier, identifier)
            .orElseThrow(() -> UserException.NotFound.byIdentifier(identifier));
        return user;
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<UserPrivateDto> getAllActiveUsers() {
        List<User> list = userRepository.findAllByActive(true);
        return list.stream()
                .map(userMapper::toPrivateDto)
                .toList();
    }

    public List<UserPrivateDto> getAllUnactiveUsers() {
        List<User> list = userRepository.findAllByActive(false);
        return list.stream()
                .map(userMapper::toPrivateDto)
                .toList();
    }

    public List<UserPrivateDto> getAllUsers() {
        List<User> list = userRepository.findAll();
        return list.stream()
                .map(userMapper::toPrivateDto)
                .toList();
    }

    @Transactional
    public User createUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw UserException.AlreadyExists.byUsername(request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw UserException.AlreadyExists.byEmail(request.email());
        }

        User newUser = new User();
        newUser.setUsername(request.username());
        newUser.setEmail(request.email());
        newUser.setUserImageUrl(request.userImageUrl());
        userRepository.save(newUser);

        userCredentialService.createLocalUserCredential(request, newUser);
        // user is still created before verification to prevent dublicate signup and spamming
        activateAccount(newUser);

        log.info("Created local user : {}", newUser.getUsername());
        
        return newUser;
    }

    @Transactional
    public void activateAccount(User user) {
        if (user.isActive()) {
            throw new UserException.AlreadyActive();
        }
        
        String fromEmail = user.getEmail();
        String username = user.getUsername();
        String token = verifyTokenService.generateToken(user.getEmail()).getToken();
        
        eventPublisher.publishEvent(
            new VerifyEmailEvent(fromEmail, username, token)
        );
    }

    @Transactional
    public void deactivateUser(Long id) {
        User deletedUser = userRepository.findById(id)
            .orElseThrow(() -> UserException.NotFound.byId(id));

        deletedUser.setActive(false);
        userRepository.save(deletedUser);

        log.info("Deactivated user : {}", deletedUser.getUsername());
    }

    @Transactional
    public User processOauth2User(String email, String name, AuthProviders provider) {
        return userRepository.findByEmail(email)
            .orElseGet(() -> createOauth2User(email, name, provider));
    }

    private User createOauth2User(String email, String name, AuthProviders provider) {
        if (userRepository.existsByUsername(name)) {
            name += UUID.randomUUID().toString().substring(0,5);
        }

        User newUser = new User();
        newUser.setUsername(name);
        newUser.setEmail(email);
        newUser.setActive(true);// no need to verify email
        userRepository.save(newUser);

        userCredentialService.createOauth2UserCredential(newUser, provider);

        log.info("Created oauth2 user : {}", newUser.getUsername());

        return newUser;
    }

    public String updateUsername(Long id, UserUpdateUsernameRequest request) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> UserException.NotFound.byId(id));

        if (!existingUser.getUsername().equals(request.username()) &&
             userRepository.existsByUsername(request.username())) {
            throw UserException.AlreadyExists.byUsername(request.username());
        }

        existingUser.setUsername(request.username());

        userRepository.save(existingUser);

        log.info("Updated username for User : {}", existingUser.getUsername());

        return existingUser.getUsername();
    }

    public String updateEmail(Long id, UserUpdateEmailRequest request) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> UserException.NotFound.byId(id));

        if (!existingUser.getEmail().equals(request.email()) &&
             userRepository.existsByEmail(request.email())) {
            throw UserException.AlreadyExists.byEmail(request.email());
        }

        existingUser.setEmail(request.email());

        userRepository.save(existingUser);

        log.info("Updated email for User : {}", existingUser.getUsername());

        return existingUser.getEmail();
    }

    public String updateImageUrl(Long id, UserUpdateImageUrlRequest request) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> UserException.NotFound.byId(id));

        existingUser.setUserImageUrl(request.imageUrl());

        userRepository.save(existingUser);

        log.info("Updated profile picture for user : {}", existingUser.getUsername());

        return existingUser.getUserImageUrl();
    }

    @Transactional
    public void updatePassword(Long id, UserUpdatePasswordRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> UserException.NotFound.byId(id));

        UserCredential localCredential = user.getUserCredentials()
            .stream()
            .filter(l -> AuthProviders.LOCAL.equals(l.getProvider()))
            .findFirst()
            .orElseThrow(() -> AuthException.Provider.notLocal());

        if (!passwordEncoder.matches(request.oldPassword(), localCredential.getPassword())) {
            throw UserException.PasswordMismatch.oldAndNew();
        }

        localCredential.setPassword(passwordEncoder.encode(request.newPassword()));

        log.info("Updated password for user : {}", user.getUsername());
    }

    @Transactional
    public void resetPassword(Long id, String password) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> UserException.NotFound.byId(id));

        UserCredential localCredential = user.getUserCredentials()
            .stream()
            .filter(l -> AuthProviders.LOCAL.equals(l.getProvider()))
            .findFirst()
            .orElseThrow(() -> AuthException.Provider.notLocal());

        localCredential.setPassword(passwordEncoder.encode(password));

        log.info("Updated password for user : {}", user.getUsername());
    }

    @Transactional
    public void updateRole(Long id, UserUpdateRoleRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> UserException.NotFound.byId(id));

        user.setRole(request.role());
        userRepository.save(user);

        refreshSecurityContext(user);

        log.info("Updated role for user : {}", user.getUsername());
    }

    private void refreshSecurityContext(User user) {
        //to-do
    }

}
