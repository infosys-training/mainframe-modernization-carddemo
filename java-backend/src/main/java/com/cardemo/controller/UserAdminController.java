package com.cardemo.controller;

import com.cardemo.dto.ApiResponse;
import com.cardemo.dto.UserCreateRequest;
import com.cardemo.model.CardDemoUser;
import com.cardemo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * User administration controller - migrated from COBOL programs COUSR00C-COUSR03C.
 *
 * All operations require admin access (original: only accessible from COADM01C admin menu).
 *
 * COUSR00C: User list - STARTBR/READNEXT on USRSEC file.
 * COUSR01C: User add - validate and WRITE to USRSEC.
 * COUSR02C: User update - READ UPDATE / REWRITE on USRSEC.
 * COUSR03C: User delete - READ UPDATE / DELETE on USRSEC.
 */
@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "User Administration", description = "User management - admin only (migrated from COUSR00C-03C)")
public class UserAdminController {

    private final UserService userService;

    public UserAdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "List all users",
               description = "Migrated from COUSR00C user list screen.")
    public ResponseEntity<ApiResponse<List<CardDemoUser>>> listUsers() {
        return ResponseEntity.ok(ApiResponse.ok(userService.listUsers()));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<CardDemoUser>> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserById(userId)));
    }

    @PostMapping
    @Operation(summary = "Add new user",
               description = "Migrated from COUSR01C user add screen. " +
                             "Validates user type: 'A' (Admin) or 'U' (User).")
    public ResponseEntity<ApiResponse<CardDemoUser>> addUser(
            @Valid @RequestBody UserCreateRequest request) {
        CardDemoUser created = userService.addUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User added successfully", created));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user",
               description = "Migrated from COUSR02C user update screen.")
    public ResponseEntity<ApiResponse<CardDemoUser>> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                "User updated successfully",
                userService.updateUser(userId, request)));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user",
               description = "Migrated from COUSR03C user delete screen.")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deleted successfully"));
    }
}
