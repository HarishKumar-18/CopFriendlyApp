package com.copfriendly.repository;

import com.copfriendly.model.Violation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ViolationRepository extends MongoRepository<Violation, String> {
    
    List<Violation> findByIssuerId(String issuerId);
    
    List<Violation> findByStatus(String status);
    
    List<Violation> findByPaymentStatus(String paymentStatus);
    
    List<Violation> findByVehicleNumber(String vehicleNumber);
    
    Optional<Violation> findByTicketNumber(String ticketNumber);
    
    @Query("{ 'createdAt': { $gte: ?0, $lt: ?1 } }")
    List<Violation> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("{ 'issuerId': ?0, 'createdAt': { $gte: ?1, $lt: ?2 } }")
    List<Violation> findByIssuerIdAndCreatedAtBetween(String issuerId, LocalDateTime start, LocalDateTime end);
    
    @Query("{ 'status': 'PENDING' }")
    List<Violation> findPendingViolations();
    
    @Query("{ 'paymentStatus': 'UNPAID' }")
    List<Violation> findUnpaidViolations();

    @Query("{ 'paymentStatus': 'UNPAID', 'status': 'APPROVED' }")
    List<Violation> findUnpaidApprovedViolations();
    
    @Query("{ 'paymentStatus': 'OVERDUE' }")
    List<Violation> findOverdueViolations();
    
    @Query("{ 'issuerId': ?0, 'status': ?1 }")
    List<Violation> findByIssuerIdAndStatus(String issuerId, String status);
    
    @Query("{ 'issuerId': ?0, 'paymentStatus': ?1 }")
    List<Violation> findByIssuerIdAndPaymentStatus(String issuerId, String paymentStatus);
    
    long countByIssuerId(String issuerId);
    
    long countByStatus(String status);
    
    long countByPaymentStatus(String paymentStatus);
    
    long countByIssuerIdAndStatus(String issuerId, String status);
    
    @Query(value = "{ 'issuerId': ?0 }", count = true)
    long countByIssuerIdAndCreatedAtBetween(String issuerId, LocalDateTime start, LocalDateTime end);
}
