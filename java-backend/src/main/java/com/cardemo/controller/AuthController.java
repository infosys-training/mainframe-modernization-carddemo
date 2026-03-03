package com.cardemo.controller;

import com.cardemo.dto.ApiResponse;
import com.cardemo.dto.LoginRequest;
import com.cardemo.dto.LoginResponse;
import com.cardemo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication controller - migrated from COBOL program COSGN00C.cbl.
 *
 * Original CICS transaction: CC00
 * Original program flow:
 *   Screen COSGN0A -> PROCESS-ENTER-KEY -> READ-USER-SEC-FILE
 *   -> XCTL to COADM01C or COMEN01C based on user type
 *
 * This REST endpoint replaces the CICS terminal signon screen with
 * a JSON-based authentication API.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication (migrated from COSGN00C)")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "User login",
               description = "Authenticate user and return menu options. " +
                             "Migrated from COSGN00C signon screen.")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.authenticate(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }
}
