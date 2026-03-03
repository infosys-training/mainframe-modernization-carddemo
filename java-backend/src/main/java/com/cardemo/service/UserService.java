package com.cardemo.service;

import com.cardemo.dto.UserCreateRequest;
import com.cardemo.exception.BusinessValidationException;
import com.cardemo.exception.ResourceNotFoundException;
import com.cardemo.model.CardDemoUser;
import com.cardemo.repository.CardDemoUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User management service - migrated from COBOL programs COUSR00C, COUSR01C, COUSR02C, COUSR03C.
 *
 * COUSR00C: List users from USRSEC file via STARTBR/READNEXT.
 * COUSR01C: Add new user - validate all fields, CICS WRITE to USRSEC.
 * COUSR02C: Update user - CICS READ UPDATE / REWRITE on USRSEC.
 * COUSR03C: Delete user - CICS READ UPDATE / DELETE on USRSEC.
 *
 * All admin-only operations (original: accessible only from COADM01C admin menu).
 */
@Service
public class UserService {

    private final CardDemoUserRepository userRepository;

    public UserService(CardDemoUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * List all users - migrated from COUSR00C.
     * Original: STARTBR/READNEXT loop on USRSEC file with page display.
     */
    public List<CardDemoUser> listUsers() {
        return userRepository.findAll();
    }

    public CardDemoUser getUserById(String userId) {
        return userRepository.findById(userId.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    /**
     * Add new user - migrated from COUSR01C.
     * Original: Validates user ID doesn't already exist (RESP=NOTFND expected),
     * then CICS WRITE to USRSEC file.
     */
    @Transactional
    public CardDemoUser addUser(UserCreateRequest request) {
        String userId = request.getUserId().toUpperCase();

        if (userRepository.findById(userId).isPresent()) {
            throw new BusinessValidationException("User ID already exists: " + userId);
        }

        CardDemoUser user = new CardDemoUser();
        user.setUserId(userId);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(request.getPassword().toUpperCase());
        user.setUserType(request.getUserType().toUpperCase());

        return userRepository.save(user);
    }

    /**
     * Update user - migrated from COUSR02C.
     * Original: CICS READ UPDATE on USRSEC, modify fields, CICS REWRITE.
     */
    @Transactional
    public CardDemoUser updateUser(String userId, UserCreateRequest request) {
        CardDemoUser existing = getUserById(userId);

        if (request.getFirstName() != null) {
            existing.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            existing.setLastName(request.getLastName());
        }
        if (request.getPassword() != null) {
            existing.setPassword(request.getPassword().toUpperCase());
        }
        if (request.getUserType() != null) {
            existing.setUserType(request.getUserType().toUpperCase());
        }

        return userRepository.save(existing);
    }

    /**
     * Delete user - migrated from COUSR03C.
     * Original: CICS READ UPDATE on USRSEC, confirm, CICS DELETE.
     */
    @Transactional
    public void deleteUser(String userId) {
        CardDemoUser user = getUserById(userId);
        userRepository.delete(user);
    }
}
