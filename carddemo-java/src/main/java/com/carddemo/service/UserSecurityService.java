package com.carddemo.service;

import com.carddemo.entity.UserSecurity;
import com.carddemo.exception.DuplicateResourceException;
import com.carddemo.repository.UserSecurityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserSecurityService {

    private final UserSecurityRepository userSecurityRepository;

    public UserSecurityService(UserSecurityRepository userSecurityRepository) {
        this.userSecurityRepository = userSecurityRepository;
    }

    @Transactional
    public UserSecurity addUser(UserSecurity user) {
        validateUserFields(user);

        if (userSecurityRepository.existsById(user.getUsrId())) {
            throw new DuplicateResourceException("User ID already exist...");
        }

        return userSecurityRepository.save(user);
    }

    public Optional<UserSecurity> findById(String usrId) {
        return userSecurityRepository.findById(usrId);
    }

    public List<UserSecurity> findAll() {
        return userSecurityRepository.findAll();
    }

    @Transactional
    public void deleteUser(String usrId) {
        userSecurityRepository.deleteById(usrId);
    }

    @Transactional
    public UserSecurity updateUser(UserSecurity user) {
        validateUserFields(user);
        return userSecurityRepository.save(user);
    }

    private void validateUserFields(UserSecurity user) {
        if (user.getUsrFname() == null || user.getUsrFname().isBlank()) {
            throw new IllegalArgumentException("First Name can NOT be empty...");
        }
        if (user.getUsrLname() == null || user.getUsrLname().isBlank()) {
            throw new IllegalArgumentException("Last Name can NOT be empty...");
        }
        if (user.getUsrId() == null || user.getUsrId().isBlank()) {
            throw new IllegalArgumentException("User ID can NOT be empty...");
        }
        if (user.getUsrPwd() == null || user.getUsrPwd().isBlank()) {
            throw new IllegalArgumentException("Password can NOT be empty...");
        }
        if (user.getUsrType() == null || user.getUsrType().isBlank()) {
            throw new IllegalArgumentException("User Type can NOT be empty...");
        }
    }
}
