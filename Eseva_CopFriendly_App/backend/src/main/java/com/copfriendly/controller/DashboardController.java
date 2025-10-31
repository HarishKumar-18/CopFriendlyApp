package com.copfriendly.controller;

import com.copfriendly.repository.ViolationRepository;
import com.copfriendly.repository.EmergencyRepository;
import com.copfriendly.repository.UserRepository;
import com.copfriendly.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DashboardController {
    
    @Autowired
    private ViolationRepository violationRepository;
    
    @Autowired
    private EmergencyRepository emergencyRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats(HttpServletRequest request) {
        try {
            String username = getUsernameFromToken(request);
            if (username == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }
            
            // Get today's date range
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
            
            // Get user details
            var user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            String userId = user.get().getId();
            
            // Calculate stats
            long totalViolations = violationRepository.countByIssuerId(userId);
            long todayViolations = violationRepository.countByIssuerIdAndCreatedAtBetween(userId, startOfDay, endOfDay);
            long pendingViolations = violationRepository.countByIssuerIdAndStatus(userId, "PENDING");
            long approvedViolations = violationRepository.countByIssuerIdAndStatus(userId, "APPROVED");
            
            long totalEmergencies = emergencyRepository.countByReporterId(userId);
            long todayEmergencies = emergencyRepository.countByReporterIdAndCreatedAtBetween(userId, startOfDay, endOfDay);
            long activeEmergencies = emergencyRepository.countActiveEmergencies();
            
            long totalUsers = userRepository.count();
            long pendingUsers = userRepository.findByStatus("pending").size();
            long approvedUsers = userRepository.findByStatus("approved").size();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("violations", Map.of(
                "total", totalViolations,
                "today", todayViolations,
                "pending", pendingViolations,
                "approved", approvedViolations
            ));
            
            stats.put("emergencies", Map.of(
                "total", totalEmergencies,
                "today", todayEmergencies,
                "active", activeEmergencies
            ));
            
            stats.put("users", Map.of(
                "total", totalUsers,
                "pending", pendingUsers,
                "approved", approvedUsers
            ));
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch dashboard stats: " + e.getMessage()));
        }
    }
    
    @GetMapping("/recent-activity")
    public ResponseEntity<?> getRecentActivity(HttpServletRequest request) {
        try {
            String username = getUsernameFromToken(request);
            if (username == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }
            
            // Get user details
            var user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            String userId = user.get().getId();
            
            // Get recent violations (last 10)
            List<Map<String, Object>> recentViolations = violationRepository.findByIssuerId(userId)
                .stream()
                .limit(10)
                .map(v -> Map.<String, Object>of(
                    "id", v.getId(),
                    "vehicleNumber", v.getVehicleNumber(),
                    "violationType", v.getViolationType(),
                    "fineAmount", v.getFineAmount(),
                    "status", v.getStatus(),
                    "createdAt", v.getCreatedAt()
                ))
                .toList();
            
            // Get recent emergencies (last 10)
            List<Map<String, Object>> recentEmergencies = emergencyRepository.findByReporterId(userId)
                .stream()
                .limit(10)
                .map(e -> Map.<String, Object>of(
                    "id", e.getId(),
                    "type", e.getType(),
                    "location", e.getLocation(),
                    "status", e.getStatus(),
                    "priority", e.getPriority(),
                    "createdAt", e.getCreatedAt()
                ))
                .toList();
            
            // Get nearby active emergencies
            List<Map<String, Object>> nearbyEmergencies = emergencyRepository.findActiveEmergencies()
                .stream()
                .limit(5)
                .map(e -> Map.<String, Object>of(
                    "id", e.getId(),
                    "type", e.getType(),
                    "location", e.getLocation(),
                    "priority", e.getPriority(),
                    "createdAt", e.getCreatedAt()
                ))
                .toList();
            
            Map<String, Object> activity = new HashMap<>();
            activity.put("violations", recentViolations);
            activity.put("emergencies", recentEmergencies);
            activity.put("nearbyEmergencies", nearbyEmergencies);
            
            return ResponseEntity.ok(activity);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch recent activity: " + e.getMessage()));
        }
    }
    
    @GetMapping("/user-stats/{userId}")
    public ResponseEntity<?> getUserStats(@PathVariable String userId) {
        try {
            // Get user details
            var user = userRepository.findById(userId);
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            // Get today's date range
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
            
            // Calculate user-specific stats
            long totalViolations = violationRepository.countByIssuerId(userId);
            long todayViolations = violationRepository.countByIssuerIdAndCreatedAtBetween(userId, startOfDay, endOfDay);
            long pendingViolations = violationRepository.countByIssuerIdAndStatus(userId, "PENDING");
            long approvedViolations = violationRepository.countByIssuerIdAndStatus(userId, "APPROVED");
            
            long totalEmergencies = emergencyRepository.countByReporterId(userId);
            long todayEmergencies = emergencyRepository.countByReporterIdAndCreatedAtBetween(userId, startOfDay, endOfDay);
            
            Map<String, Object> userStats = new HashMap<>();
            userStats.put("userId", userId);
            userStats.put("username", user.get().getUsername());
            userStats.put("fullName", user.get().getFullName());
            userStats.put("role", user.get().getRole());
            userStats.put("violations", Map.of(
                "total", totalViolations,
                "today", todayViolations,
                "pending", pendingViolations,
                "approved", approvedViolations
            ));
            userStats.put("emergencies", Map.of(
                "total", totalEmergencies,
                "today", todayEmergencies
            ));
            
            return ResponseEntity.ok(userStats);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch user stats: " + e.getMessage()));
        }
    }
    
    @GetMapping("/overview")
    public ResponseEntity<?> getSystemOverview() {
        try {
            // Get system-wide stats
            long totalViolations = violationRepository.count();
            long pendingViolations = violationRepository.countByStatus("PENDING");
            long approvedViolations = violationRepository.countByStatus("APPROVED");
            long unpaidViolations = violationRepository.countByPaymentStatus("UNPAID");
            
            long totalEmergencies = emergencyRepository.count();
            long activeEmergencies = emergencyRepository.countActiveEmergencies();
            long sosAlerts = emergencyRepository.countByType("SOS");
            
            long totalUsers = userRepository.count();
            
            Map<String, Object> overview = new HashMap<>();
            overview.put("violations", Map.of(
                "total", totalViolations,
                "pending", pendingViolations,
                "approved", approvedViolations,
                "unpaid", unpaidViolations
            ));
            overview.put("emergencies", Map.of(
                "total", totalEmergencies,
                "active", activeEmergencies,
                "sosAlerts", sosAlerts
            ));
            overview.put("users", Map.of(
                "total", totalUsers
            ));
            
            return ResponseEntity.ok(overview);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch system overview: " + e.getMessage()));
        }
    }
    
    private String getUsernameFromToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                return jwtUtil.extractUsername(token);
            }
        } catch (Exception e) {
            // Log error if needed
        }
        return null;
    }
}
