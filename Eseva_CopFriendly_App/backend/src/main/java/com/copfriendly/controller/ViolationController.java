package com.copfriendly.controller;

import com.copfriendly.dto.ViolationRequest;
import com.copfriendly.model.Violation;
import com.copfriendly.repository.ViolationRepository;
import com.copfriendly.repository.UserRepository;
import com.copfriendly.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/violations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ViolationController {
    
    @Autowired
    private ViolationRepository violationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private com.copfriendly.service.ViolationService violationService;

    @PutMapping("/{id}/pay")
    public ResponseEntity<?> payViolationById(@PathVariable String id, jakarta.servlet.http.HttpServletRequest request) {
        try {
            // Allow public header or token with PUBLIC role
            boolean allowed = false;
            String publicHeader = request.getHeader("X-Public-User");
            
            // Allow if header is present (any truthy value)
            if (publicHeader != null && !publicHeader.isEmpty()) {
                allowed = true;
            }

            String auth = request.getHeader("Authorization");
            if (!allowed && auth != null && auth.startsWith("Bearer ")) {
                String token = auth.substring(7);
                try {
                    if (jwtUtil.validateToken(token)) {
                        String role = jwtUtil.getRoleFromToken(token);
                        if (role != null && role.equalsIgnoreCase("PUBLIC")) allowed = true;
                    }
                } catch (Exception ex) {
                    // ignore
                }
            }

            if (!allowed) {
                return ResponseEntity.status(403).body(Map.of("error", "Forbidden: public role required"));
            }

            Optional<Violation> violationOpt = violationRepository.findById(id);
            if (violationOpt.isEmpty()) return ResponseEntity.notFound().build();

            Violation violation = violationOpt.get();
            if (!"APPROVED".equalsIgnoreCase(violation.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Payment allowed only after inspector approval"));
            }
            if ("PAID".equalsIgnoreCase(violation.getPaymentStatus())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Violation already paid"));
            }

            Violation updated = violationService.markAsPaid(violation);
            return ResponseEntity.ok(Map.of("message", "Payment recorded", "violation", updated));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to process payment: " + e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createViolation(@Valid @RequestBody ViolationRequest violationRequest, HttpServletRequest request) {
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
            
            // Create violation
            Violation violation = new Violation();
            violation.setVehicleNumber(violationRequest.getVehicleNumber());
            violation.setVehicleType(violationRequest.getVehicleType());
            violation.setVehicleModel(violationRequest.getVehicleModel());
            violation.setVehicleColor(violationRequest.getVehicleColor());
            violation.setDriverName(violationRequest.getDriverName());
            violation.setLicenseNumber(violationRequest.getLicenseNumber());
            violation.setDriverPhoneNumber(violationRequest.getDriverPhoneNumber());
            violation.setDriverAddress(violationRequest.getDriverAddress());
            violation.setViolationType(violationRequest.getViolationType());
            violation.setFineAmount(violationRequest.getFineAmount());
            violation.setLocation(violationRequest.getLocation());
            violation.setDescription(violationRequest.getDescription());
            violation.setEvidence(violationRequest.getEvidence());
            violation.setLatitude(violationRequest.getLatitude());
            violation.setLongitude(violationRequest.getLongitude());
            violation.setGpsLocation(violationRequest.getGpsLocation());
            violation.setRemarks(violationRequest.getRemarks());
            violation.setIsDraft(violationRequest.getIsDraft());
            violation.setIssuerId(user.get().getId());
            violation.setIssuerName(user.get().getFullName());
            violation.setStatus("PENDING");
            violation.setPaymentStatus("UNPAID");
            violation.setDueDate(LocalDateTime.now().plusDays(30)); // 30 days from now
            violation.setTicketNumber(generateTicketNumber());
            violation.setCreatedAt(LocalDateTime.now());
            violation.setUpdatedAt(LocalDateTime.now());

            // Set unique violationId
            violation.setViolationId(java.util.UUID.randomUUID().toString());

            // Parse violation date time if provided
            if (violationRequest.getViolationDateTime() != null && !violationRequest.getViolationDateTime().isEmpty()) {
                try {
                    LocalDateTime violationDateTime = LocalDateTime.parse(violationRequest.getViolationDateTime());
                    violation.setViolationDateTime(violationDateTime);
                } catch (Exception e) {
                    // If parsing fails, use current time
                    violation.setViolationDateTime(LocalDateTime.now());
                }
            } else {
                violation.setViolationDateTime(LocalDateTime.now());
            }
            
            Violation savedViolation = violationRepository.save(violation);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Violation created successfully");
            response.put("violation", savedViolation);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create violation: " + e.getMessage()));
        }
    }
    
    @PostMapping("/draft")
    public ResponseEntity<?> saveDraft(@Valid @RequestBody ViolationRequest violationRequest, HttpServletRequest request) {
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
            
            // Create violation draft
            Violation violation = new Violation();
            violation.setVehicleNumber(violationRequest.getVehicleNumber());
            violation.setVehicleType(violationRequest.getVehicleType());
            violation.setVehicleModel(violationRequest.getVehicleModel());
            violation.setVehicleColor(violationRequest.getVehicleColor());
            violation.setDriverName(violationRequest.getDriverName());
            violation.setLicenseNumber(violationRequest.getLicenseNumber());
            violation.setDriverPhoneNumber(violationRequest.getDriverPhoneNumber());
            violation.setDriverAddress(violationRequest.getDriverAddress());
            violation.setViolationType(violationRequest.getViolationType());
            violation.setFineAmount(violationRequest.getFineAmount());
            violation.setLocation(violationRequest.getLocation());
            violation.setDescription(violationRequest.getDescription());
            violation.setEvidence(violationRequest.getEvidence());
            violation.setLatitude(violationRequest.getLatitude());
            violation.setLongitude(violationRequest.getLongitude());
            violation.setGpsLocation(violationRequest.getGpsLocation());
            violation.setRemarks(violationRequest.getRemarks());
            violation.setIsDraft(true); // Mark as draft
            violation.setIssuerId(user.get().getId());
            violation.setIssuerName(user.get().getFullName());
            violation.setStatus("DRAFT");
            violation.setPaymentStatus("UNPAID");
            violation.setCreatedAt(LocalDateTime.now());
            violation.setUpdatedAt(LocalDateTime.now());
            
            // Parse violation date time if provided
            if (violationRequest.getViolationDateTime() != null && !violationRequest.getViolationDateTime().isEmpty()) {
                try {
                    LocalDateTime violationDateTime = LocalDateTime.parse(violationRequest.getViolationDateTime());
                    violation.setViolationDateTime(violationDateTime);
                } catch (Exception e) {
                    violation.setViolationDateTime(LocalDateTime.now());
                }
            } else {
                violation.setViolationDateTime(LocalDateTime.now());
            }
            
            Violation savedViolation = violationRepository.save(violation);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Violation draft saved successfully");
            response.put("violation", savedViolation);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to save draft: " + e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllViolations(HttpServletRequest request) {
        try {
            // For the dashboard and demo flows we allow read-only access to the
            // full violations list without requiring a token here. Authentication
            // is handled by SecurityConfig for production; this controller method
            // previously demanded a token (via getUsernameFromToken) which caused
            // anonymous requests (or requests without a valid token) to fail with
            // a 400. Returning the list here ensures approved violations already
            // stored in the DB are visible on the inspector dashboard.
            List<Violation> violations = violationRepository.findAll();
            return ResponseEntity.ok(Map.of("violations", violations));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch violations: " + e.getMessage()));
        }
    }
    
    @GetMapping("/my")
    public ResponseEntity<?> getMyViolations(HttpServletRequest request) {
        try {
            String username = getUsernameFromToken(request);
            if (username == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }
            
            var user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            List<Violation> violations = violationRepository.findByIssuerId(user.get().getId());
            return ResponseEntity.ok(Map.of("violations", violations));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch violations: " + e.getMessage()));
        }
    }
    
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingViolations() {
        try {
            List<Violation> violations = violationRepository.findPendingViolations();
            return ResponseEntity.ok(Map.of("violations", violations));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch pending violations: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getViolationById(@PathVariable String id) {
        try {
            Optional<Violation> violation = violationRepository.findById(id);
            if (violation.isPresent()) {
                return ResponseEntity.ok(Map.of("violation", violation.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch violation: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveViolation(@PathVariable String id) {
        try {
            Optional<Violation> violationOpt = violationRepository.findById(id);
            if (violationOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Violation violation = violationOpt.get();
            violation.setStatus("APPROVED");
            violation.setUpdatedAt(LocalDateTime.now());
            
            Violation updatedViolation = violationRepository.save(violation);
            return ResponseEntity.ok(Map.of("message", "Violation approved", "violation", updatedViolation));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to approve violation: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectViolation(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            Optional<Violation> violationOpt = violationRepository.findById(id);
            if (violationOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Violation violation = violationOpt.get();
            violation.setStatus("REJECTED");
            violation.setRemarks(request.getOrDefault("reason", "Rejected"));
            violation.setUpdatedAt(LocalDateTime.now());
            
            Violation updatedViolation = violationRepository.save(violation);
            return ResponseEntity.ok(Map.of("message", "Violation rejected", "violation", updatedViolation));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to reject violation: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/payment-status")
    public ResponseEntity<?> updatePaymentStatus(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            Optional<Violation> violationOpt = violationRepository.findById(id);
            if (violationOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Violation violation = violationOpt.get();
            String status = request.get("status");
            violation.setPaymentStatus(status);
            
            if ("PAID".equals(status)) {
                violation.setPaidDate(LocalDateTime.now());
            }
            
            violation.setUpdatedAt(LocalDateTime.now());
            
            Violation updatedViolation = violationRepository.save(violation);
            return ResponseEntity.ok(Map.of("message", "Payment status updated", "violation", updatedViolation));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update payment status: " + e.getMessage()));
        }
    }
    
    @GetMapping("/issuer/{issuerId}")
    public ResponseEntity<?> getViolationsByIssuer(@PathVariable String issuerId) {
        try {
            List<Violation> violations = violationRepository.findByIssuerId(issuerId);
            return ResponseEntity.ok(Map.of("violations", violations));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch violations: " + e.getMessage()));
        }
    }
    
    @GetMapping("/unpaid")
    public ResponseEntity<?> getUnpaidViolations(jakarta.servlet.http.HttpServletRequest request) {
        try {
            // allow public access if header present or JWT role PUBLIC
            String publicHeader = request.getHeader("X-Public-User");
            boolean allowed = false;
            
            // Allow if header is present (any truthy value)
            if (publicHeader != null && !publicHeader.isEmpty()) {
                allowed = true;
            }
            
            String auth = request.getHeader("Authorization");
            if (!allowed && auth != null && auth.startsWith("Bearer ")) {
                String token = auth.substring(7);
                try {
                    if (jwtUtil.validateToken(token)) {
                        String role = jwtUtil.getRoleFromToken(token);
                        if (role != null && role.equalsIgnoreCase("PUBLIC")) allowed = true;
                    }
                } catch (Exception ex) {
                    // ignore
                }
            }

            if (!allowed) {
                // Not a public request; require authentication (existing behavior) - return 403 to indicate forbidden
                return ResponseEntity.status(403).body(Map.of("error", "Forbidden: public role required"));
            }

            List<Violation> violations = violationRepository.findUnpaidApprovedViolations();
            return ResponseEntity.ok(Map.of("violations", violations));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch unpaid violations: " + e.getMessage()));
        }
    }
    
    @GetMapping("/overdue")
    public ResponseEntity<?> getOverdueViolations() {
        try {
            List<Violation> violations = violationRepository.findOverdueViolations();
            return ResponseEntity.ok(Map.of("violations", violations));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch overdue violations: " + e.getMessage()));
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
    
    private String generateTicketNumber() {
        return "TK" + System.currentTimeMillis();
    }
}
