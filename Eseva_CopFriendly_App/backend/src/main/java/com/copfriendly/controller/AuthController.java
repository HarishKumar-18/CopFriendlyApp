package com.copfriendly.controller;

import com.copfriendly.dto.AuthResponse;
import com.copfriendly.dto.LoginRequest;
import com.copfriendly.dto.RegisterRequest;
import com.copfriendly.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            AuthResponse response = authService.register(registerRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage() != null ? e.getMessage() : "Invalid username or password");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Auth endpoint is working!");
        return ResponseEntity.ok(response);
    }

    /**
     * Safe, minimal username existence check.
     * Returns 200 { "exists": true } or { "exists": false }.
     * This endpoint is intentionally minimal to avoid leaking user details.
     */
    @GetMapping("/exists")
    public ResponseEntity<?> exists(@RequestParam(name = "username", required = true) String username) {
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "username query parameter is required"));
        }
        try {
            boolean exists = authService.usernameExists(username);
            return ResponseEntity.ok(Map.of("exists", exists));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal error"));
        }
    }

    @GetMapping("/users/pending")
    public ResponseEntity<?> listPendingUsers() {
        try {
            var users = authService.listPendingUsers();
            return ResponseEntity.ok(users != null ? users : java.util.Collections.emptyList());
        } catch (Exception e) {
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }
    }

    @PostMapping("/users/{id}/approve")
    public ResponseEntity<?> approveUser(@PathVariable String id) {
        try {
            boolean success = authService.approveUser(id);
            if (!success) return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            return ResponseEntity.ok(Map.of("message", "User approved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/users/{id}/reject")
    public ResponseEntity<?> rejectUser(@PathVariable String id) {
        try {
            boolean success = authService.rejectUser(id);
            if (!success) return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            return ResponseEntity.ok(Map.of("message", "User rejected successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
