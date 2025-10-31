package com.copfriendly.dto;

import jakarta.validation.constraints.NotBlank;

public class EmergencyRequest {
    
    @NotBlank(message = "Emergency type is required")
    private String type;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    private String landmark;
    
    private Double latitude;
    
    private Double longitude;
    
    private String description;
    
    private String severityLevel;
    
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
    
    private String evidence;
    
    // Constructors
    public EmergencyRequest() {}
    
    public EmergencyRequest(String type, String location) {
        this.type = type;
        this.location = location;
    }
    
    // Getters and Setters
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
    
    public Boolean getRequiresPolice() {
        return requiresPolice;
    }
    
    public void setRequiresPolice(Boolean requiresPolice) {
        this.requiresPolice = requiresPolice;
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
    
    public Boolean getRequiresTrafficControl() {
        return requiresTrafficControl;
    }
    
    public void setRequiresTrafficControl(Boolean requiresTrafficControl) {
        this.requiresTrafficControl = requiresTrafficControl;
    }
    
    public String getReporterName() {
        return reporterName;
    }
    
    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public String getAdditionalNotes() {
        return additionalNotes;
    }
    
    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }
    
    public String getEvidence() {
        return evidence;
    }
    
    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }
}
