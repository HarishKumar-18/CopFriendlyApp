package com.copfriendly.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Document(collection = "emergencydb")
public class Emergency {
    
    @Id
    private String id;
    
    @NotBlank(message = "Emergency type is required")
    private String type; // SOS, ACCIDENT, MEDICAL, TRAFFIC_JAM, etc.
    
    @NotBlank(message = "Location is required")
    private String location;
    
    private String landmark;
    
    private Double latitude;
    
    private Double longitude;
    
    private String description;
    
    private String severityLevel; // LOW, MEDIUM, HIGH, CRITICAL
    
    private Integer casualties = 0;
    
    private Integer vehiclesInvolved = 0;
    
    private Boolean requiresPolice = false;
    
    private Boolean requiresAmbulance = false;
    
    private Boolean requiresFireService = false;
    
    private Boolean requiresTrafficControl = false;
    
    @NotBlank(message = "Reporter name is required")
    private String reporterName;
    
    @NotBlank(message = "Reporter contact is required")
    private String contactNumber;
    
    private String additionalNotes;
    
    private String vehicleNumber;
    
    private String incidentType;
    
    @NotBlank(message = "Reporter ID is required")
    private String reporterId;
    
    private String status = "ACTIVE"; // ACTIVE, RESPONDED, RESOLVED, CANCELLED
    
    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, CRITICAL
    
    private String assignedUnitId;
    
    private String assignedDriverId;
    
    private String assignedUnitName;
    
    private LocalDateTime responseTime;
    
    private LocalDateTime resolvedTime;
    
    private String resolutionNotes;
    
    private String evidence; // File path or URL
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Constructors
    public Emergency() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Emergency(String type, String location, String reporterId) {
        this();
        this.type = type;
        this.location = location;
        this.reporterId = reporterId;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getLandmark() {
        return landmark;
    }
    
    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public String getReporterId() {
        return reporterId;
    }
    
    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }
    
    public String getReporterName() {
        return reporterName;
    }
    
    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getSeverityLevel() {
        return severityLevel;
    }
    
    public void setSeverityLevel(String severityLevel) {
        this.severityLevel = severityLevel;
    }
    
    public Integer getCasualties() {
        return casualties;
    }
    
    public void setCasualties(Integer casualties) {
        this.casualties = casualties;
    }
    
    public Integer getVehiclesInvolved() {
        return vehiclesInvolved;
    }
    
    public void setVehiclesInvolved(Integer vehiclesInvolved) {
        this.vehiclesInvolved = vehiclesInvolved;
    }
    
    public Boolean getRequiresTrafficControl() {
        return requiresTrafficControl;
    }
    
    public void setRequiresTrafficControl(Boolean requiresTrafficControl) {
        this.requiresTrafficControl = requiresTrafficControl;
    }
    
    public String getAdditionalNotes() {
        return additionalNotes;
    }
    
    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public String getAssignedUnitId() {
        return assignedUnitId;
    }
    
    public void setAssignedUnitId(String assignedUnitId) {
        this.assignedUnitId = assignedUnitId;
    }
    
    public String getAssignedDriverId() {
        return assignedDriverId;
    }
    
    public void setAssignedDriverId(String assignedDriverId) {
        this.assignedDriverId = assignedDriverId;
    }
    
    public String getAssignedUnitName() {
        return assignedUnitName;
    }
    
    public void setAssignedUnitName(String assignedUnitName) {
        this.assignedUnitName = assignedUnitName;
    }
    
    public LocalDateTime getResponseTime() {
        return responseTime;
    }
    
    public void setResponseTime(LocalDateTime responseTime) {
        this.responseTime = responseTime;
    }
    
    public LocalDateTime getResolvedTime() {
        return resolvedTime;
    }
    
    public void setResolvedTime(LocalDateTime resolvedTime) {
        this.resolvedTime = resolvedTime;
    }
    
    public String getResolutionNotes() {
        return resolutionNotes;
    }
    
    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public String getVehicleNumber() {
        return vehicleNumber;
    }
    
    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
    
    public String getIncidentType() {
        return incidentType;
    }
    
    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }
    
    public Boolean getRequiresAmbulance() {
        return requiresAmbulance;
    }
    
    public void setRequiresAmbulance(Boolean requiresAmbulance) {
        this.requiresAmbulance = requiresAmbulance;
    }
    
    public Boolean getRequiresFireService() {
        return requiresFireService;
    }
    
    public void setRequiresFireService(Boolean requiresFireService) {
        this.requiresFireService = requiresFireService;
    }
    
    public Boolean getRequiresPolice() {
        return requiresPolice;
    }
    
    public void setRequiresPolice(Boolean requiresPolice) {
        this.requiresPolice = requiresPolice;
    }
    
    public String getEvidence() {
        return evidence;
    }
    
    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Emergency{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", location='" + location + '\'' +
                ", reporterId='" + reporterId + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
