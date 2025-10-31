package com.copfriendly.service;

import com.copfriendly.model.Violation;
import com.copfriendly.repository.ViolationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ViolationService {

    @Autowired
    private ViolationRepository violationRepository;

    public List<Violation> getUnpaidViolations() {
        // Only expose unpaid violations that are already approved
        return violationRepository.findUnpaidApprovedViolations();
    }

    public List<Violation> getPaidViolations() {
        return violationRepository.findByPaymentStatus("PAID");
    }

    public Optional<Violation> findById(String id) {
        return violationRepository.findById(id);
    }

    public Violation markAsPaid(Violation violation) {
        violation.setPaymentStatus("PAID");
        violation.setPaidDate(LocalDateTime.now());
        violation.setUpdatedAt(LocalDateTime.now());
        return violationRepository.save(violation);
    }
}
