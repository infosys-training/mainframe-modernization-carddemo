package com.carddemo.userauth;

import com.carddemo.common.entity.UserSecurity;
import com.carddemo.common.exception.BusinessException;
import com.carddemo.userauth.dto.LoginRequest;
import com.carddemo.userauth.dto.LoginResponse;
import com.carddemo.userauth.repository.UserSecurityRepository;
import com.carddemo.userauth.service.UserAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAuthServiceTest {

    @Mock private UserSecurityRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserAuthService userAuthService;

    private UserSecurity adminUser;
    private UserSecurity regularUser;

    @BeforeEach
    void setUp() {
        adminUser = new UserSecurity("ADMIN001");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setPassword("$2a$10$hashedpassword");
        adminUser.setUserType("A");

        regularUser = new UserSecurity("USER0001");
        regularUser.setFirstName("Regular");
        regularUser.setLastName("User");
        regularUser.setPassword("PASSWORD");
        regularUser.setUserType("U");
    }

    @Test
    void authenticate_validCredentials_returnsAuthenticated() {
        when(userRepository.findById("ADMIN001")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("PASSWORD", "$2a$10$hashedpassword")).thenReturn(true);

        LoginResponse response = userAuthService.authenticate(new LoginRequest("ADMIN001", "PASSWORD"));
        assertTrue(response.isAuthenticated());
        assertEquals("A", response.getUserType());
    }

    @Test
    void authenticate_invalidCredentials_returnsNotAuthenticated() {
        when(userRepository.findById("ADMIN001")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("WRONG", "$2a$10$hashedpassword")).thenReturn(false);

        LoginResponse response = userAuthService.authenticate(new LoginRequest("ADMIN001", "WRONG"));
        assertFalse(response.isAuthenticated());
    }

    @Test
    void authenticate_legacyPassword_rehashesToBCrypt() {
        when(userRepository.findById("USER0001")).thenReturn(Optional.of(regularUser));
        when(passwordEncoder.matches("PASSWORD", "PASSWORD")).thenReturn(false);
        when(passwordEncoder.encode("PASSWORD")).thenReturn("$2a$10$newHash");

        LoginResponse response = userAuthService.authenticate(new LoginRequest("USER0001", "PASSWORD"));
        assertTrue(response.isAuthenticated());
        verify(userRepository).save(regularUser);
        assertEquals("$2a$10$newHash", regularUser.getPassword());
    }

    @Test
    void authenticate_unknownUser_returnsNotAuthenticated() {
        when(userRepository.findById("UNKNOWN")).thenReturn(Optional.empty());

        LoginResponse response = userAuthService.authenticate(new LoginRequest("UNKNOWN", "PASSWORD"));
        assertFalse(response.isAuthenticated());
    }

    @Test
    void createUser_duplicateUserId_throwsBusinessException() {
        when(userRepository.existsById("ADMIN001")).thenReturn(true);

        UserSecurity newUser = new UserSecurity("ADMIN001");
        newUser.setPassword("pass");
        assertThrows(BusinessException.class, () -> userAuthService.createUser(newUser));
    }

    @Test
    void createUser_newUser_hashesPassword() {
        when(userRepository.existsById("NEWUSER1")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("$2a$10$hash");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UserSecurity newUser = new UserSecurity("NEWUSER1");
        newUser.setPassword("password");
        newUser.setUserType("U");

        UserSecurity saved = userAuthService.createUser(newUser);
        assertEquals("$2a$10$hash", saved.getPassword());
    }

    @Test
    void userSecurity_isAdmin_correctType() {
        assertTrue(adminUser.isAdmin());
        assertFalse(adminUser.isRegularUser());
        assertTrue(regularUser.isRegularUser());
        assertFalse(regularUser.isAdmin());
    }
}
