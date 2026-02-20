package com.carddemo.service;

import com.carddemo.entity.UserSecurity;
import com.carddemo.exception.DuplicateResourceException;
import com.carddemo.repository.UserSecurityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSecurityServiceTest {

    @Mock
    private UserSecurityRepository userSecurityRepository;

    @InjectMocks
    private UserSecurityService userSecurityService;

    private UserSecurity validUser;

    @BeforeEach
    void setUp() {
        validUser = new UserSecurity();
        validUser.setUsrId("testuser");
        validUser.setUsrFname("John");
        validUser.setUsrLname("Doe");
        validUser.setUsrPwd("pass1234");
        validUser.setUsrType("R");
    }

    @Test
    void addUser_success() {
        when(userSecurityRepository.existsById("testuser")).thenReturn(false);
        when(userSecurityRepository.save(any(UserSecurity.class))).thenReturn(validUser);

        UserSecurity result = userSecurityService.addUser(validUser);
        assertEquals("testuser", result.getUsrId());
        assertEquals("John", result.getUsrFname());
    }

    @Test
    void addUser_duplicateId_throwsException() {
        when(userSecurityRepository.existsById("testuser")).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> userSecurityService.addUser(validUser));
    }

    @Test
    void addUser_emptyFirstName_throwsException() {
        validUser.setUsrFname("");
        assertThrows(IllegalArgumentException.class, () -> userSecurityService.addUser(validUser));
    }

    @Test
    void addUser_emptyLastName_throwsException() {
        validUser.setUsrLname("");
        assertThrows(IllegalArgumentException.class, () -> userSecurityService.addUser(validUser));
    }

    @Test
    void addUser_emptyUserId_throwsException() {
        validUser.setUsrId("");
        assertThrows(IllegalArgumentException.class, () -> userSecurityService.addUser(validUser));
    }

    @Test
    void addUser_emptyPassword_throwsException() {
        validUser.setUsrPwd("");
        assertThrows(IllegalArgumentException.class, () -> userSecurityService.addUser(validUser));
    }

    @Test
    void addUser_emptyUserType_throwsException() {
        validUser.setUsrType("");
        assertThrows(IllegalArgumentException.class, () -> userSecurityService.addUser(validUser));
    }

    @Test
    void findById_success() {
        when(userSecurityRepository.findById("testuser")).thenReturn(Optional.of(validUser));
        Optional<UserSecurity> result = userSecurityService.findById("testuser");
        assertEquals(true, result.isPresent());
        assertEquals("testuser", result.get().getUsrId());
    }
}
