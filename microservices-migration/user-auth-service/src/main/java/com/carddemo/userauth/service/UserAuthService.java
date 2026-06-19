package com.carddemo.userauth.service;

import com.carddemo.common.entity.UserSecurity;
import com.carddemo.common.exception.BusinessException;
import com.carddemo.common.exception.ResourceNotFoundException;
import com.carddemo.userauth.dto.LoginRequest;
import com.carddemo.userauth.dto.LoginResponse;
import com.carddemo.userauth.repository.UserSecurityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User authentication and management service.
 * Migrated from COSGN00C (sign-on), COUSR00C-03C (user CRUD),
 * COMEN01C (main menu), COADM01C (admin menu).
 * Replaces plain-text PIC X(08) password with BCrypt hashing.
 */
@Service
@Transactional
public class UserAuthService {

    private final UserSecurityRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAuthService(UserSecurityRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse authenticate(LoginRequest request) {
        UserSecurity user = userRepository.findById(request.getUserId().toUpperCase())
                .orElse(null);

        if (user == null) {
            return new LoginResponse(request.getUserId(), null, null, null, false);
        }

        boolean passwordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
        // Legacy support: check plain-text match during migration period
        if (!passwordMatch && request.getPassword().equals(user.getPassword())) {
            passwordMatch = true;
            // Rehash with BCrypt on successful legacy login
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(user);
        }

        return new LoginResponse(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUserType(),
                passwordMatch
        );
    }

    @Transactional(readOnly = true)
    public UserSecurity getUser(String userId) {
        return userRepository.findById(userId.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    @Transactional(readOnly = true)
    public Page<UserSecurity> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<UserSecurity> listAdmins() {
        return userRepository.findAllAdmins();
    }

    public UserSecurity createUser(UserSecurity user) {
        if (userRepository.existsById(user.getUserId().toUpperCase())) {
            throw new BusinessException("User already exists: " + user.getUserId());
        }
        user.setUserId(user.getUserId().toUpperCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public UserSecurity updateUser(String userId, UserSecurity updates) {
        UserSecurity user = getUser(userId);
        if (updates.getFirstName() != null) user.setFirstName(updates.getFirstName());
        if (updates.getLastName() != null) user.setLastName(updates.getLastName());
        if (updates.getUserType() != null) user.setUserType(updates.getUserType());
        return userRepository.save(user);
    }

    public void changePassword(String userId, String newPassword) {
        UserSecurity user = getUser(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deleteUser(String userId) {
        UserSecurity user = getUser(userId);
        userRepository.delete(user);
    }
}
