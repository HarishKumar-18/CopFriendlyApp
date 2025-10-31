package com.copfriendly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class ViolationRequest {
    
    @NotBlank(message = "Vehicle number is required")
    private String vehicleNumber;
    
    private String vehicleType;
    
    private String vehicleModel;
    
    private String vehicleColor;
    
    @NotBlank(message = "Driver name is required")
    private String driverName;
    
    private String licenseNumber;
    
    private String driverPhoneNumber;
    
    private String driverAddress;
    
    @NotBlank(message = "Violation type is required")
    private String violationType;
    
    @NotNull(message = "Fine amount is required")
    @Min(value = 0, message = "Fine amount must be positive")
    private Double fineAmount;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    private String violationDateTime;
    
    private String description;
    
    private String evidence;
    
    private Double latitude;
    
    private Double longitude;
    
    private String gpsLocation;
    
    private String remarks;
    
    private Boolean isDraft = false;
    
    // Constructors
    public ViolationRequest() {}
    
    public ViolationRequest(String vehicleNumber, String violationType, String location, Double fineAmount) {
        this.vehicleNumber = vehicleNumber;
        this.violationType = violationType;
        this.location = location;
        this.fineAmount = fineAmount;
    }
    
    // Getters and Setters
    public String getVehicleNumber() {
        return vehicleNumber;
    }
    
    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
    
    public String getVehicleType() {
        return vehicleType;
    }
    
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
    
    public String getVehicleModel() {
        return vehicleModel;
    }
    
    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }
    
    public String getVehicleColor() {
        return vehicleColor;
    }
    
    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }
    
    public String getDriverName() {
        return driverName;
    }
    
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
    
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    
    public String getDriverPhoneNumber() {
        return driverPhoneNumber;
    }
    
    public void setDriverPhoneNumber(String driverPhoneNumber) {
        this.driverPhoneNumber = driverPhoneNumber;
    }
    
    public String getDriverAddress() {
        return driverAddress;
    }
    
    public void setDriverAddress(String driverAddress) {
        this.driverAddress = driverAddress;
    }
    
    public String getViolationType() {
        return violationType;
    }
    
    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getViolationDateTime() {
        return violationDateTime;
    }
    
    public void setViolationDateTime(String violationDateTime) {
        this.violationDateTime = violationDateTime;
    }
    
    public Double getFineAmount() {
        return fineAmount;
    }
    
    public void setFineAmount(Double fineAmount) {
        this.fineAmount = fineAmount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getEvidence() {
        return evidence;
    }
    
    public void setEvidence(String evidence) {
        this.evidence = evidence;
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
    
    public String getGpsLocation() {
        return gpsLocation;
    }
    
    public void setGpsLocation(String gpsLocation) {
        this.gpsLocation = gpsLocation;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public Boolean getIsDraft() {
        return isDraft;
    }
    
    public void setIsDraft(Boolean isDraft) {
        this.isDraft = isDraft;
    }
}
