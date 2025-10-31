package com.copfriendly.repository;

import com.copfriendly.model.Emergency;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmergencyRepository extends MongoRepository<Emergency, String> {
    
    List<Emergency> findByReporterId(String reporterId);
    
    List<Emergency> findByStatus(String status);
    
    List<Emergency> findByType(String type);
    
    List<Emergency> findByPriority(String priority);
    
    List<Emergency> findByAssignedUnitId(String assignedUnitId);
    
    @Query("{ 'status': 'ACTIVE' }")
    List<Emergency> findActiveEmergencies();
    
    @Query("{ 'type': 'SOS' }")
    List<Emergency> findSOSAlerts();
    
    @Query("{ 'type': 'SOS', 'status': 'ACTIVE' }")
    List<Emergency> findActiveSOSAlerts();
    
    @Query("{ 'createdAt': { $gte: ?0, $lt: ?1 } }")
    List<Emergency> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("{ 'reporterId': ?0, 'createdAt': { $gte: ?1, $lt: ?2 } }")
    List<Emergency> findByReporterIdAndCreatedAtBetween(String reporterId, LocalDateTime start, LocalDateTime end);
    
    @Query("{ 'latitude': { $gte: ?0, $lte: ?2 }, 'longitude': { $gte: ?1, $lte: ?3 }, 'status': 'ACTIVE' }")
    List<Emergency> findNearbyActiveEmergencies(Double minLat, Double minLng, Double maxLat, Double maxLng);
    
    @Query("{ 'reporterId': ?0, 'type': 'SOS', 'status': 'ACTIVE' }")
    Optional<Emergency> findActiveSOSByReporterId(String reporterId);
    
    @Query("{ 'assignedUnitId': ?0, 'status': 'ACTIVE' }")
    List<Emergency> findActiveEmergenciesByAssignedUnit(String assignedUnitId);
    
    long countByReporterId(String reporterId);
    
    long countByStatus(String status);
    
    long countByType(String type);
    
    @Query(value = "{ 'reporterId': ?0 }", count = true)
    long countByReporterIdAndCreatedAtBetween(String reporterId, LocalDateTime start, LocalDateTime end);
    
    @Query(value = "{ 'status': 'ACTIVE' }", count = true)
    long countActiveEmergencies();
}
