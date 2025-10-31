package com.copfriendly.controller;

import com.copfriendly.model.User;
import com.copfriendly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    // Get all pending users who have requested login approval
    @GetMapping("/pending-requests")
    public ResponseEntity<?> getPendingLoginRequests() {
        try {
            List<User> pendingRequests = userRepository.findAll().stream()
                .filter(u -> "pending".equalsIgnoreCase(u.getStatus()) && u.isLoginRequested())
                .toList();
            return ResponseEntity.ok(Map.of("users", pendingRequests));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch pending login requests: " + e.getMessage()));
        }
    }
    
    @Autowired
    private UserRepository userRepository;
    
    
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                User userData = user.get();
                Map<String, Object> response = new HashMap<>();
                response.put("id", userData.getId());
                response.put("username", userData.getUsername());
                response.put("email", userData.getEmail());
                response.put("fullName", userData.getFullName());
                response.put("firstName", userData.getFirstName());
                response.put("lastName", userData.getLastName());
                response.put("phone", userData.getPhone());
                response.put("dateOfBirth", userData.getDateOfBirth());
                response.put("role", userData.getRole());
                response.put("createdAt", userData.getCreatedAt());
                response.put("active", userData.isActive());
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get user profile");
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(Map.of("users", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch users: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        try {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                return ResponseEntity.ok(Map.of("user", user.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch user: " + e.getMessage()));
        }
    }
    
    @GetMapping("/role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            List<User> users = userRepository.findAll().stream()
                .filter(user -> role.equals(user.getRole()))
                .toList();
            return ResponseEntity.ok(Map.of("users", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch users by role: " + e.getMessage()));
        }
    }
    
    @GetMapping("/on-duty")
    public ResponseEntity<?> getOnDutyUsers() {
        try {
            // For now, return all active users as "on duty"
            // In a real system, you'd have a separate duty tracking mechanism
            List<User> onDutyUsers = userRepository.findAll().stream()
                .filter(User::isActive)
                .toList();
            return ResponseEntity.ok(Map.of("users", onDutyUsers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch on-duty users: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/duty-status")
    public ResponseEntity<?> updateDutyStatus(@PathVariable String id, @RequestBody Map<String, Boolean> request) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            User user = userOpt.get();
            
            // Check if user is updating their own status or if they're an inspector/admin
            Optional<User> currentUserOpt = userRepository.findByUsername(currentUsername);
            if (currentUserOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Current user not found"));
            }
            
            User currentUser = currentUserOpt.get();
            boolean isSelf = user.getUsername().equals(currentUsername);
            boolean isInspectorOrAdmin = currentUser.getRole().equalsIgnoreCase("traffic_inspector") || 
                                         currentUser.getRole().equalsIgnoreCase("admin");
            
            if (!isSelf && !isInspectorOrAdmin) {
                return ResponseEntity.status(403).body(Map.of("error", "You can only update your own duty status"));
            }
            Boolean onDuty = request.get("status");
            
            // Update user's duty status
            if (onDuty != null && onDuty) {
                user.setDutyStatus("On Duty");
                user.setActive(true);
            } else {
                user.setDutyStatus("Off Duty");
                user.setActive(false);
            }
            user.setUpdatedAt(LocalDateTime.now());
            
            User updatedUser = userRepository.save(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Duty status updated successfully");
            response.put("user", updatedUser);
            response.put("dutyStatus", updatedUser.getDutyStatus());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update duty status: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody Map<String, Object> request) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            User user = userOpt.get();
            
            // Update fields if provided
            if (request.containsKey("username")) {
                String newUsername = (String) request.get("username");
                if (newUsername != null && !newUsername.isBlank()) {
                    // If it's different, ensure uniqueness
                    if (!newUsername.equals(user.getUsername()) && userRepository.existsByUsername(newUsername)) {
                        return ResponseEntity.badRequest().body(Map.of("error", "Username is already taken"));
                    }
                    user.setUsername(newUsername);
                }
            }
            if (request.containsKey("fullName")) {
                user.setFullName((String) request.get("fullName"));
            }
            if (request.containsKey("firstName")) {
                user.setFirstName((String) request.get("firstName"));
            }
            if (request.containsKey("lastName")) {
                user.setLastName((String) request.get("lastName"));
            }
            if (request.containsKey("phone")) {
                user.setPhone((String) request.get("phone"));
            }
            if (request.containsKey("email")) {
                user.setEmail((String) request.get("email"));
            }
            if (request.containsKey("role")) {
                user.setRole((String) request.get("role"));
            }
            if (request.containsKey("active")) {
                user.setActive((Boolean) request.get("active"));
            }
            
            user.setUpdatedAt(LocalDateTime.now());
            
            User updatedUser = userRepository.save(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User updated successfully");
            response.put("user", updatedUser);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update user: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            userRepository.deleteById(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete user: " + e.getMessage()));
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "User endpoint is working!");
        return ResponseEntity.ok(response);
    }
    
    // Admin endpoints for user management
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingUsers() {
        try {
            List<User> pendingUsers = userRepository.findByStatus("pending");
            return ResponseEntity.ok(Map.of("users", pendingUsers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch pending users: " + e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getUsersByStatus(@PathVariable String status) {
        try {
            List<User> users = userRepository.findByStatus(status);
            return ResponseEntity.ok(Map.of("users", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch users by status: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            User user = userOpt.get();
            String newStatus = request.get("status");
            
            if (newStatus == null || newStatus.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Status is required"));
            }
            
            // Normalize and persist status
            user.setStatus(newStatus);

            // Defensively ensure active flag matches approved/rejected/suspended statuses.
            // Some code paths or direct DB edits may leave 'status' and 'active' inconsistent,
            // so enforce active=true for approved users to guarantee they can authenticate.
            if ("approved".equalsIgnoreCase(newStatus) || "approve".equalsIgnoreCase(newStatus)) {
                user.setActive(true);
            } else if ("rejected".equalsIgnoreCase(newStatus) || "suspended".equalsIgnoreCase(newStatus)) {
                user.setActive(false);
            }
            
            user.setUpdatedAt(LocalDateTime.now());
            User updatedUser = userRepository.save(user);
            
            // Extra safety: if the saved user's status is approved but active is still false,
            // adjust and save again. This handles unexpected cases where repository hooks
            // or concurrent updates may have changed the record between read/save.
            if ("approved".equalsIgnoreCase(updatedUser.getStatus()) && !updatedUser.isActive()) {
                updatedUser.setActive(true);
                updatedUser.setUpdatedAt(LocalDateTime.now());
                updatedUser = userRepository.save(updatedUser);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User status updated successfully");
            response.put("user", updatedUser);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update user status: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            User user = userOpt.get();
            String newRole = request.get("role");
            
            if (newRole == null || newRole.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Role is required"));
            }
            
            user.setRole(newRole);
            user.setUpdatedAt(LocalDateTime.now());
            User updatedUser = userRepository.save(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User role updated successfully");
            response.put("user", updatedUser);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update user role: " + e.getMessage()));
        }
    }
}
