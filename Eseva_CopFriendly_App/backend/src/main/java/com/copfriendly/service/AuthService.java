package com.copfriendly.service;

import com.copfriendly.model.User;
import com.copfriendly.dto.AuthResponse;
import com.copfriendly.dto.RegisterRequest;
import com.copfriendly.dto.LoginRequest;
import com.copfriendly.repository.UserRepository;
import com.copfriendly.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest registerRequest) {
        // Derive username from firstName (server-side). This ignores any client-provided username
        String rawFirst = registerRequest.getFirstName() == null ? "" : registerRequest.getFirstName().trim();
        if (rawFirst.isEmpty()) {
            throw new RuntimeException("First name is required to generate username");
        }
        // sanitize: keep letters and numbers only, lower-case
        String baseUsername = rawFirst.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
        if (baseUsername.length() < 2) {
            // fallback to using full name or a uuid fragment
            baseUsername = (registerRequest.getFullName() == null ? "user" : registerRequest.getFullName().replaceAll("[^A-Za-z0-9]", "").toLowerCase());
            if (baseUsername.length() < 2) {
                baseUsername = "user" + java.util.UUID.randomUUID().toString().substring(0,4);
            }
        }
        String candidate = baseUsername;
        int suffix = 0;
        while (userRepository.existsByUsername(candidate)) {
            suffix++;
            candidate = baseUsername + suffix; // simple numeric suffix
            // safety: avoid infinite loop
            if (suffix > 1000) {
                candidate = baseUsername + java.util.UUID.randomUUID().toString().substring(0,6);
                break;
            }
        }
        String finalUsername = candidate;
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
    User user = new User();
    user.setUsername(finalUsername);
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setPhone(registerRequest.getPhone());
        user.setDateOfBirth(registerRequest.getDateOfBirth());
        user.setRole(registerRequest.getRole());
        user.setConfirmPassword(registerRequest.getConfirmPassword());
        user.setActive(false);  // Inactive until admin approves
        user.setStatus("pending");  // Pending until admin approves
    // Ensure userId is set to a unique, non-null value to satisfy unique index
    user.setUserId("user-" + java.util.UUID.randomUUID().toString());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        // Don't return token - user needs admin approval first
        return new AuthResponse(
            savedUser.getId(),  // Include user ID
            null,  // No token until approved
            savedUser.getUsername(),
            savedUser.getEmail(),
            savedUser.getFullName(),
            savedUser.getRole()
        );
    }

    public AuthResponse login(LoginRequest loginRequest) {
        // First, check if user exists
        User user = userRepository.findByUsername(loginRequest.getUsername().trim())
            .orElseThrow(() -> new RuntimeException("User not found"));
        // If suspended, show a clear message (before password check)
        if ("suspended".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("Your account has been suspended. Please contact the administrator.");
        }
        
        // Enforce approval for all users (including admins)
        if ("pending".equalsIgnoreCase(user.getStatus())) {
            if (!user.isLoginRequested()) {
                user.setLoginRequested(true);
                user.setUpdatedAt(java.time.LocalDateTime.now());
                userRepository.save(user);
            }
            throw new RuntimeException("You are registered but you need approval from admin to login.");
        }
        
        // Authenticate password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername().trim(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(
            user.getId(),  // Include user ID
            token,
            user.getUsername(),
            user.getEmail(),
            user.getFullName(),
            user.getRole()
        );
    }

    public java.util.List<User> listPendingUsers() {
        return userRepository.findByStatus("pending");
    }

    public boolean approveUser(String id) {
        var userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) return false;
        var user = userOpt.get();
        user.setStatus("approved");
        user.setActive(true);
        user.setLoginRequested(false);  // Reset login request flag
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return true;
    }

    public boolean rejectUser(String id) {
        var userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) return false;
        var user = userOpt.get();
        user.setStatus("rejected");
        user.setActive(false);
        user.setLoginRequested(false);  // Reset login request flag
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return true;
    }

    // Check whether a username exists in the repository. Trim and normalize input.
    public boolean usernameExists(String username) {
        if (username == null) return false;
        String cleaned = username.trim();
        if (cleaned.isEmpty()) return false;
        return userRepository.existsByUsername(cleaned);
    }
}
