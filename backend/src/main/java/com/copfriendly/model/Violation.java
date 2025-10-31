package com.copfriendly.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

@Document(collection = "violations")
public class Violation {
    @Indexed(unique = true)
    private String violationId;
    
    @Id
    private String id;
    
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
    
    private String vehicleOwner;
    
    private String contactNumber;
    
    @NotBlank(message = "Violation type is required")
    private String violationType;
    
    @NotNull(message = "Fine amount is required")
    @Min(value = 0, message = "Fine amount must be positive")
    private Double fineAmount;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    private LocalDateTime violationDateTime;
    
    private String description;
    
    private String evidence; // File path or URL
    
    private Double latitude;
    
    private Double longitude;
    
    private String gpsLocation;
    
    @NotBlank(message = "Issuer ID is required")
    private String issuerId;
    
    private String issuerName;
    
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED, PAID
    
    private String paymentStatus = "UNPAID"; // UNPAID, PAID, OVERDUE
    
    private LocalDateTime dueDate;
    
    private LocalDateTime paidDate;
    
    private String ticketNumber;
    
    private String remarks;
    
    private Boolean isDraft = false;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Constructors
    public Violation() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Violation(String vehicleNumber, String violationType, String location, Double fineAmount, String issuerId) {
        this();
        this.vehicleNumber = vehicleNumber;
        this.violationType = violationType;
        this.location = location;
        this.fineAmount = fineAmount;
        this.issuerId = issuerId;
    }
    
    // Getters and Setters
    public String getViolationId() {
        return violationId;
    }

    public void setViolationId(String violationId) {
        this.violationId = violationId;
    }
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
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
    
    public LocalDateTime getViolationDateTime() {
        return violationDateTime;
    }
    
    public void setViolationDateTime(LocalDateTime violationDateTime) {
        this.violationDateTime = violationDateTime;
    }
    
    public Double getFineAmount() {
        return fineAmount;
    }
    
    public void setFineAmount(Double fineAmount) {
        this.fineAmount = fineAmount;
    }
    
    public String getIssuerId() {
        return issuerId;
    }
    
    public void setIssuerId(String issuerId) {
        this.issuerId = issuerId;
    }
    
    public String getIssuerName() {
        return issuerName;
    }
    
    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
    
    public LocalDateTime getPaidDate() {
        return paidDate;
    }
    
    public void setPaidDate(LocalDateTime paidDate) {
        this.paidDate = paidDate;
    }
    
    public String getTicketNumber() {
        return ticketNumber;
    }
    
    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }
    
    public String getVehicleOwner() {
        return vehicleOwner;
    }
    
    public void setVehicleOwner(String vehicleOwner) {
        this.vehicleOwner = vehicleOwner;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
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
        return "Violation{" +
                "id='" + id + '\'' +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", violationType='" + violationType + '\'' +
                ", location='" + location + '\'' +
                ", fineAmount=" + fineAmount +
                ", issuerId='" + issuerId + '\'' +
                ", status='" + status + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
