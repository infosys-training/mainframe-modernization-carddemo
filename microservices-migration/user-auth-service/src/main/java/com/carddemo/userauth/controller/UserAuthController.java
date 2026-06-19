package com.carddemo.userauth.controller;

import com.carddemo.common.dto.ApiResponse;
import com.carddemo.common.entity.UserSecurity;
import com.carddemo.userauth.dto.LoginRequest;
import com.carddemo.userauth.dto.LoginResponse;
import com.carddemo.userauth.service.UserAuthService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserAuthController {

    private final UserAuthService userAuthService;

    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse response = userAuthService.authenticate(request);
        if (!response.isAuthenticated()) {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid credentials"));
        }
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserSecurity>> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.success(userAuthService.getUser(userId)));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserSecurity>>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                userAuthService.listUsers(PageRequest.of(page, size, Sort.by("userId")))));
    }

    @GetMapping("/users/admins")
    public ResponseEntity<ApiResponse<List<UserSecurity>>> listAdmins() {
        return ResponseEntity.ok(ApiResponse.success(userAuthService.listAdmins()));
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserSecurity>> createUser(@RequestBody UserSecurity user) {
        return ResponseEntity.ok(ApiResponse.success(userAuthService.createUser(user)));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserSecurity>> updateUser(
            @PathVariable String userId, @RequestBody UserSecurity updates) {
        return ResponseEntity.ok(ApiResponse.success(userAuthService.updateUser(userId, updates)));
    }

    @PutMapping("/users/{userId}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable String userId, @RequestBody Map<String, String> body) {
        userAuthService.changePassword(userId, body.get("newPassword"));
        return ResponseEntity.ok(ApiResponse.success("Password changed", null));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId) {
        userAuthService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User deleted", null));
    }
}
