package com.cardemo.service;

import com.cardemo.dto.LoginRequest;
import com.cardemo.dto.LoginResponse;
import com.cardemo.dto.LoginResponse.MenuOption;
import com.cardemo.exception.AuthenticationException;
import com.cardemo.model.CardDemoUser;
import com.cardemo.repository.CardDemoUserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Authentication service - migrated from COBOL program COSGN00C.cbl.
 *
 * Original COBOL flow:
 * 1. PROCESS-ENTER-KEY: Validates user ID and password are not empty
 * 2. READ-USER-SEC-FILE: Reads USRSEC VSAM file by user ID key
 * 3. Compares SEC-USR-PWD with entered password
 * 4. On success, sets CDEMO-USER-ID and CDEMO-USER-TYPE in COMMAREA
 * 5. XCTL to COADM01C (admin) or COMEN01C (user) based on user type
 *
 * Also incorporates menu options from:
 * - COMEN02Y.cpy (CARDDEMO-MAIN-MENU-OPTIONS) for regular users
 * - COADM02Y.cpy (CARDDEMO-ADMIN-MENU-OPTIONS) for admin users
 */
@Service
public class AuthService {

    private final CardDemoUserRepository userRepository;

    public AuthService(CardDemoUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LoginResponse authenticate(LoginRequest request) {
        String userId = request.getUserId().toUpperCase().trim();
        String password = request.getPassword().toUpperCase().trim();

        CardDemoUser user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationException("User not found. Try again ..."));

        if (!user.getPassword().equals(password)) {
            throw new AuthenticationException("Wrong Password. Try again ...");
        }

        LoginResponse response = new LoginResponse();
        response.setUserId(user.getUserId());
        response.setUserType(user.getUserType());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setMessage("Login successful");

        if (user.isAdmin()) {
            response.setMenuOptions(getAdminMenuOptions());
        } else {
            response.setMenuOptions(getUserMenuOptions());
        }

        return response;
    }

    /**
     * Regular user menu - migrated from COMEN02Y.cpy.
     * Original: 10 options defined as FILLER records with PIC fields.
     */
    private List<MenuOption> getUserMenuOptions() {
        List<MenuOption> options = new ArrayList<>();
        options.add(new MenuOption(1, "Account View", "/api/accounts/{id}"));
        options.add(new MenuOption(2, "Account Update", "/api/accounts/{id}"));
        options.add(new MenuOption(3, "Credit Card List", "/api/cards"));
        options.add(new MenuOption(4, "Credit Card View", "/api/cards/{cardNum}"));
        options.add(new MenuOption(5, "Credit Card Update", "/api/cards/{cardNum}"));
        options.add(new MenuOption(6, "Transaction List", "/api/transactions"));
        options.add(new MenuOption(7, "Transaction View", "/api/transactions/{id}"));
        options.add(new MenuOption(8, "Transaction Add", "/api/transactions"));
        options.add(new MenuOption(9, "Transaction Reports", "/api/reports/transactions"));
        options.add(new MenuOption(10, "Bill Payment", "/api/bill-payments"));
        return options;
    }

    /**
     * Admin menu - migrated from COADM02Y.cpy.
     * Original: 4 options for user management (COUSR00C-COUSR03C).
     */
    private List<MenuOption> getAdminMenuOptions() {
        List<MenuOption> options = new ArrayList<>();
        options.add(new MenuOption(1, "User List (Security)", "/api/admin/users"));
        options.add(new MenuOption(2, "User Add (Security)", "/api/admin/users"));
        options.add(new MenuOption(3, "User Update (Security)", "/api/admin/users/{id}"));
        options.add(new MenuOption(4, "User Delete (Security)", "/api/admin/users/{id}"));
        return options;
    }
}
