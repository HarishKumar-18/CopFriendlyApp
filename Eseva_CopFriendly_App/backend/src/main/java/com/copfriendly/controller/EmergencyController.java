package com.copfriendly.controller;

import com.copfriendly.dto.EmergencyRequest;
import com.copfriendly.model.Emergency;
import com.copfriendly.repository.EmergencyRepository;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/emergencies")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EmergencyController {
    private static final Logger log = LoggerFactory.getLogger(EmergencyController.class);
    
    @Autowired
    private EmergencyRepository emergencyRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping
    public ResponseEntity<?> createEmergency(@Valid @RequestBody EmergencyRequest emergencyRequest, HttpServletRequest request) {
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
            
            // Create emergency
            Emergency emergency = new Emergency();
            emergency.setType(emergencyRequest.getType());
            emergency.setLocation(emergencyRequest.getLocation());
            emergency.setLandmark(emergencyRequest.getLandmark());
            emergency.setLatitude(emergencyRequest.getLatitude());
            emergency.setLongitude(emergencyRequest.getLongitude());
            emergency.setDescription(emergencyRequest.getDescription());
            emergency.setSeverityLevel(emergencyRequest.getSeverityLevel());
            emergency.setCasualties(emergencyRequest.getCasualties());
            emergency.setVehiclesInvolved(emergencyRequest.getVehiclesInvolved());
            emergency.setRequiresPolice(emergencyRequest.getRequiresPolice());
            emergency.setRequiresAmbulance(emergencyRequest.getRequiresAmbulance());
            emergency.setRequiresFireService(emergencyRequest.getRequiresFireService());
            emergency.setRequiresTrafficControl(emergencyRequest.getRequiresTrafficControl());
            emergency.setReporterName(emergencyRequest.getReporterName());
            emergency.setContactNumber(emergencyRequest.getContactNumber());
            emergency.setAdditionalNotes(emergencyRequest.getAdditionalNotes());
            emergency.setEvidence(emergencyRequest.getEvidence());
            emergency.setReporterId(user.get().getId());
            emergency.setStatus("ACTIVE");
            emergency.setCreatedAt(LocalDateTime.now());
            emergency.setUpdatedAt(LocalDateTime.now());
            
            Emergency savedEmergency = emergencyRepository.save(emergency);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Emergency created successfully");
            response.put("emergency", savedEmergency);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error in createEmergency", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to create emergency: " + e.getMessage()));
        }
    }
    
    @PostMapping("/sos")
    public ResponseEntity<?> triggerSOS(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        try {
            String username = getUsernameFromToken(httpRequest);
            if (username == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }
            
            // Get user details
            var user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            // Check if user already has an active SOS
            Optional<Emergency> existingSOS = emergencyRepository.findActiveSOSByReporterId(user.get().getId());
            if (existingSOS.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "SOS already active"));
            }
            
            // Create SOS emergency (defensive parsing for latitude/longitude)
            Emergency sos = new Emergency();
            sos.setType("SOS");
            sos.setLocation("Emergency Location");
            // parse latitude
            Double lat = null;
            Double lng = null;
            try {
                Object latObj = request.get("latitude");
                Object lngObj = request.get("longitude");
                if (latObj instanceof Number) lat = ((Number) latObj).doubleValue();
                else if (latObj instanceof String) lat = Double.valueOf((String) latObj);
                if (lngObj instanceof Number) lng = ((Number) lngObj).doubleValue();
                else if (lngObj instanceof String) lng = Double.valueOf((String) lngObj);
            } catch (Exception pe) {
                log.warn("Failed to parse lat/lng from SOS request: {}", pe.getMessage());
            }
            sos.setLatitude(lat);
            sos.setLongitude(lng);
            sos.setReporterId(user.get().getId());
            sos.setReporterName(user.get().getFullName());
            sos.setDescription("SOS Alert - Officer needs immediate backup");
            sos.setPriority("CRITICAL");
            sos.setStatus("ACTIVE");
            sos.setRequiresPolice(true);
            sos.setCreatedAt(LocalDateTime.now());
            sos.setUpdatedAt(LocalDateTime.now());
            
            Emergency savedSOS = emergencyRepository.save(sos);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "SOS alert sent successfully");
            response.put("sos", savedSOS);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error in triggerSOS", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send SOS: " + e.getMessage()));
        }
    }
    
    @PostMapping("/sos/cancel")
    public ResponseEntity<?> cancelSOS(HttpServletRequest request) {
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
            
            // Find and cancel active SOS
            Optional<Emergency> activeSOS = emergencyRepository.findActiveSOSByReporterId(user.get().getId());
            if (activeSOS.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No active SOS found"));
            }
            
            Emergency sos = activeSOS.get();
            sos.setStatus("CANCELLED");
            sos.setUpdatedAt(LocalDateTime.now());
            
            Emergency updatedSOS = emergencyRepository.save(sos);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "SOS cancelled successfully");
            response.put("sos", updatedSOS);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to cancel SOS: " + e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllEmergencies(HttpServletRequest request) {
        try {
            String username = getUsernameFromToken(request);
            if (username == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }
            
            List<Emergency> emergencies = emergencyRepository.findAll();
            return ResponseEntity.ok(Map.of("emergencies", emergencies));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch emergencies: " + e.getMessage()));
        }
    }
    
    @GetMapping("/active")
    public ResponseEntity<?> getActiveEmergencies() {
        try {
            List<Emergency> emergencies = emergencyRepository.findActiveEmergencies();
            return ResponseEntity.ok(Map.of("emergencies", emergencies));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch active emergencies: " + e.getMessage()));
        }
    }
    
    @GetMapping("/sos")
    public ResponseEntity<?> getSOSAlerts() {
        try {
            List<Emergency> sosAlerts = emergencyRepository.findSOSAlerts();
            return ResponseEntity.ok(Map.of("sosAlerts", sosAlerts));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch SOS alerts: " + e.getMessage()));
        }
    }
    
    @GetMapping("/my")
    public ResponseEntity<?> getMyEmergencies(HttpServletRequest request) {
        try {
            String username = getUsernameFromToken(request);
            if (username == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }
            
            var user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            List<Emergency> emergencies = emergencyRepository.findByReporterId(user.get().getId());
            return ResponseEntity.ok(Map.of("emergencies", emergencies));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch emergencies: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmergencyById(@PathVariable String id) {
        try {
            Optional<Emergency> emergency = emergencyRepository.findById(id);
            if (emergency.isPresent()) {
                return ResponseEntity.ok(Map.of("emergency", emergency.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch emergency: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/dispatch")
    public ResponseEntity<?> dispatchTeam(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            Optional<Emergency> emergencyOpt = emergencyRepository.findById(id);
            if (emergencyOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Emergency emergency = emergencyOpt.get();
            emergency.setAssignedUnitId(request.get("unitId"));
            emergency.setAssignedDriverId(request.get("driverId"));
            emergency.setAssignedUnitName(request.get("unitName"));
            emergency.setStatus("RESPONDED");
            emergency.setResponseTime(LocalDateTime.now());
            emergency.setUpdatedAt(LocalDateTime.now());
            
            Emergency updatedEmergency = emergencyRepository.save(emergency);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Team dispatched successfully");
            response.put("emergency", updatedEmergency);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error in dispatchTeam", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to dispatch team: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/resolve")
    public ResponseEntity<?> resolveEmergency(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            Optional<Emergency> emergencyOpt = emergencyRepository.findById(id);
            if (emergencyOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Emergency emergency = emergencyOpt.get();
            emergency.setStatus("RESOLVED");
            emergency.setResolvedTime(LocalDateTime.now());
            emergency.setResolutionNotes(request.getOrDefault("notes", "Emergency resolved"));
            emergency.setUpdatedAt(LocalDateTime.now());
            
            Emergency updatedEmergency = emergencyRepository.save(emergency);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Emergency resolved successfully");
            response.put("emergency", updatedEmergency);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error in resolveEmergency", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to resolve emergency: " + e.getMessage()));
        }
    }
    
    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyEmergencies(@RequestParam Double latitude, @RequestParam Double longitude, @RequestParam(defaultValue = "0.01") Double radius) {
        try {
            // Calculate bounding box for nearby emergencies
            Double minLat = latitude - radius;
            Double maxLat = latitude + radius;
            Double minLng = longitude - radius;
            Double maxLng = longitude + radius;
            
            List<Emergency> nearbyEmergencies = emergencyRepository.findNearbyActiveEmergencies(minLat, minLng, maxLat, maxLng);
            
            return ResponseEntity.ok(Map.of("emergencies", nearbyEmergencies));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch nearby emergencies: " + e.getMessage()));
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<?> getEmergencyStats() {
        try {
            long totalEmergencies = emergencyRepository.count();
            long activeEmergencies = emergencyRepository.countActiveEmergencies();
            long sosAlerts = emergencyRepository.countByType("SOS");
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalEmergencies", totalEmergencies);
            stats.put("activeEmergencies", activeEmergencies);
            stats.put("sosAlerts", sosAlerts);
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch emergency stats: " + e.getMessage()));
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
