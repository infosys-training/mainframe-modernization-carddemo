package com.carddemo.controller;

import com.carddemo.entity.UserSecurity;
import com.carddemo.service.UserSecurityService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserSecurityService userSecurityService;

    public UserController(UserSecurityService userSecurityService) {
        this.userSecurityService = userSecurityService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody UserSecurity user) {
        UserSecurity saved = userSecurityService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "User " + saved.getUsrId() + " has been added ...",
                "user", saved
        ));
    }

    @GetMapping
    public ResponseEntity<List<UserSecurity>> getAllUsers() {
        return ResponseEntity.ok(userSecurityService.findAll());
    }

    @GetMapping("/{usrId}")
    public ResponseEntity<UserSecurity> getUser(@PathVariable String usrId) {
        return userSecurityService.findById(usrId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{usrId}")
    public ResponseEntity<UserSecurity> updateUser(@PathVariable String usrId,
                                                   @RequestBody UserSecurity user) {
        user.setUsrId(usrId);
        return ResponseEntity.ok(userSecurityService.updateUser(user));
    }

    @DeleteMapping("/{usrId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String usrId) {
        userSecurityService.deleteUser(usrId);
        return ResponseEntity.noContent().build();
    }
}
