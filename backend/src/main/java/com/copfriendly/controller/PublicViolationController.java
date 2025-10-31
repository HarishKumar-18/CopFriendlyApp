package com.copfriendly.controller;

import com.copfriendly.model.Violation;
import com.copfriendly.service.ViolationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/public/violations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PublicViolationController {

    private static final Logger logger = LoggerFactory.getLogger(PublicViolationController.class);

    @Autowired
    private ViolationService violationService;

    @Autowired
    private com.copfriendly.util.JwtUtil jwtUtil;

    // GET /api/public/violations/unpaid
    @GetMapping("/unpaid")
    public ResponseEntity<?> getUnpaid(jakarta.servlet.http.HttpServletRequest request) {
        try {
            if (!isPublicRequest(request)) {
                return ResponseEntity.status(403).body(Map.of("error", "Forbidden: public role required"));
            }

            List<Violation> unpaid = violationService.getUnpaidViolations();
            return ResponseEntity.ok(Map.of("violations", unpaid));
        } catch (Exception e) {
            logger.error("Error in getUnpaid()", e);
            Map<String, String> resp = new HashMap<>();
            resp.put("error", "Failed to fetch unpaid violations: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    // GET /api/public/violations/paid
    @GetMapping("/paid")
    public ResponseEntity<?> getPaid(jakarta.servlet.http.HttpServletRequest request) {
        try {
            if (!isPublicRequest(request)) {
                return ResponseEntity.status(403).body(Map.of("error", "Forbidden: public role required"));
            }

            List<Violation> paid = violationService.getPaidViolations();
            return ResponseEntity.ok(Map.of("violations", paid));
        } catch (Exception e) {
            logger.error("Error in getPaid()", e);
            Map<String, String> resp = new HashMap<>();
            resp.put("error", "Failed to fetch paid violations: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    // POST /api/public/violations/{id}/pay
    @PostMapping("/{id}/pay")
    public ResponseEntity<?> payViolation(@PathVariable String id, jakarta.servlet.http.HttpServletRequest request) {
        try {
            if (!isPublicRequest(request)) {
                return ResponseEntity.status(403).body(Map.of("error", "Forbidden: public role required"));
            }

            Optional<Violation> vOpt = violationService.findById(id);
            if (vOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Violation v = vOpt.get();
            if (!"APPROVED".equalsIgnoreCase(v.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Payment allowed only after inspector approval"));
            }
            if ("PAID".equalsIgnoreCase(v.getPaymentStatus())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Violation already paid"));
            }

            Violation updated = violationService.markAsPaid(v);
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Payment recorded");
            resp.put("violation", updated);
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            logger.error("Error in payViolation({})", id, e);
            Map<String, String> resp = new HashMap<>();
            resp.put("error", "Failed to process payment: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    // Helper: allow requests that either contain a valid JWT with role=PUBLIC or a demo header X-Public-User: true
    private boolean isPublicRequest(jakarta.servlet.http.HttpServletRequest request) {
        try {
            String publicHeader = request.getHeader("X-Public-User");
            logger.info("X-Public-User header: {}", publicHeader);
            
            // Allow if header is present (any truthy value)
            if (publicHeader != null && !publicHeader.isEmpty()) {
                return true;
            }

            String auth = request.getHeader("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) {
                String token = auth.substring(7);
                try {
                    if (jwtUtil != null && jwtUtil.validateToken(token)) {
                        String role = jwtUtil.getRoleFromToken(token);
                        if (role != null && role.equalsIgnoreCase("PUBLIC")) {
                            return true;
                        }
                    }
                } catch (Exception ex) {
                    logger.warn("Token validation failed", ex);
                }
            }
        } catch (Exception e) {
            logger.error("Error checking public request", e);
        }
        return false;
    }
}
