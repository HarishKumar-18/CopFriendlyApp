# Automated Violation Workflow Test Script
# Run this AFTER logging in and getting a JWT token

param(
    [string]$BackendUrl = "http://localhost:8080",
    [string]$Token = ""
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "🧪 VIOLATION WORKFLOW AUTOMATED TESTS" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Check if token is provided
if ([string]::IsNullOrEmpty($Token)) {
    Write-Host "⚠️  No token provided. Testing without authentication..." -ForegroundColor Yellow
    Write-Host "   To test with auth, run: .\test-violation-workflow.ps1 -Token 'your-jwt-token'`n" -ForegroundColor Yellow
}

$headers = @{
    "Content-Type" = "application/json"
}

if (-not [string]::IsNullOrEmpty($Token)) {
    $headers["Authorization"] = "Bearer $Token"
}

$testResults = @()

# ============================================
# Test 1: Backend Health Check
# ============================================
Write-Host "Test 1: Backend Health Check" -ForegroundColor Yellow
try {
    $health = Invoke-WebRequest -Uri "$BackendUrl/api/auth/login" -Method OPTIONS -TimeoutSec 5 -ErrorAction Stop
    Write-Host "✅ Backend is responding" -ForegroundColor Green
    $testResults += @{Test = "Backend Health"; Status = "PASS"}
} catch {
    Write-Host "❌ Backend is not responding. Is it running on $BackendUrl?" -ForegroundColor Red
    $testResults += @{Test = "Backend Health"; Status = "FAIL"}
    Write-Host "`nExiting tests...`n" -ForegroundColor Red
    exit 1
}

Write-Host ""

# ============================================
# Test 2: Create Complete Violation (Happy Path)
# ============================================
Write-Host "Test 2: Create Complete Violation (Happy Path)" -ForegroundColor Yellow

$completeViolation = @{
    vehicleNumber = "MH01AB1234"
    vehicleType = "car"
    vehicleModel = "Maruti Swift"
    vehicleColor = "White"
    driverName = "John Doe"
    licenseNumber = "DL123456789"
    driverPhoneNumber = "9876543210"
    driverAddress = "123 Main Street, Mumbai"
    violationType = "speeding"
    fineAmount = 2000
    location = "MG Road Junction"
    violationDateTime = (Get-Date).ToString("yyyy-MM-ddTHH:mm")
    description = "Vehicle exceeded speed limit by 40 kmph in 60 kmph zone"
    evidence = $null
    latitude = 19.076090
    longitude = 72.877426
    gpsLocation = "19.076090,72.877426"
    remarks = $null
    isDraft = $false
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$BackendUrl/api/violations" -Method POST -Body $completeViolation -Headers $headers
    Write-Host "✅ Complete violation created successfully" -ForegroundColor Green
    Write-Host "   Ticket Number: $($response.violation.ticketNumber)" -ForegroundColor Cyan
    Write-Host "   Status: $($response.violation.status)" -ForegroundColor Cyan
    Write-Host "   Payment Status: $($response.violation.paymentStatus)" -ForegroundColor Cyan
    $testResults += @{Test = "Complete Violation"; Status = "PASS"; TicketNumber = $response.violation.ticketNumber}
} catch {
    Write-Host "❌ Failed to create complete violation" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.ErrorDetails.Message) {
        $errorMsg = $_.ErrorDetails.Message | ConvertFrom-Json
        Write-Host "   Details: $($errorMsg.message)" -ForegroundColor Red
    }
    $testResults += @{Test = "Complete Violation"; Status = "FAIL"}
}

Write-Host ""

# ============================================
# Test 3: Save Draft (Partial Violation)
# ============================================
Write-Host "Test 3: Save Draft (Partial Violation)" -ForegroundColor Yellow

$draftViolation = @{
    vehicleNumber = "KA05MN9876"
    vehicleType = "motorcycle"
    vehicleModel = ""
    vehicleColor = ""
    driverName = "Jane Smith"
    licenseNumber = ""
    driverPhoneNumber = ""
    driverAddress = ""
    violationType = "other"
    fineAmount = 0
    location = "N/A"
    violationDateTime = (Get-Date).ToString("yyyy-MM-ddTHH:mm")
    description = ""
    evidence = $null
    latitude = $null
    longitude = $null
    gpsLocation = $null
    remarks = "Draft"
    isDraft = $true
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$BackendUrl/api/violations/draft" -Method POST -Body $draftViolation -Headers $headers
    Write-Host "✅ Draft violation saved successfully" -ForegroundColor Green
    Write-Host "   Ticket Number: $($response.violation.ticketNumber)" -ForegroundColor Cyan
    Write-Host "   Status: $($response.violation.status)" -ForegroundColor Cyan
    Write-Host "   Is Draft: $($response.violation.isDraft)" -ForegroundColor Cyan
    $testResults += @{Test = "Draft Violation"; Status = "PASS"; TicketNumber = $response.violation.ticketNumber}
} catch {
    Write-Host "❌ Failed to save draft violation" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.ErrorDetails.Message) {
        $errorMsg = $_.ErrorDetails.Message | ConvertFrom-Json
        Write-Host "   Details: $($errorMsg.message)" -ForegroundColor Red
    }
    $testResults += @{Test = "Draft Violation"; Status = "FAIL"}
}

Write-Host ""

# ============================================
# Test 4: List All Violations
# ============================================
Write-Host "Test 4: List All Violations" -ForegroundColor Yellow

try {
    $violations = Invoke-RestMethod -Uri "$BackendUrl/api/violations" -Method GET -Headers $headers
    $count = if ($violations -is [array]) { $violations.Count } else { 1 }
    Write-Host "✅ Retrieved $count violation(s)" -ForegroundColor Green
    
    if ($count -gt 0) {
        Write-Host "`n   Recent violations:" -ForegroundColor Cyan
        $violations | Select-Object -First 3 | ForEach-Object {
            Write-Host "   - $($_.ticketNumber): $($_.vehicleNumber) | $($_.violationType) | ₹$($_.fineAmount) | $($_.status)" -ForegroundColor White
        }
    }
    $testResults += @{Test = "List Violations"; Status = "PASS"; Count = $count}
} catch {
    Write-Host "❌ Failed to list violations" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    $testResults += @{Test = "List Violations"; Status = "FAIL"}
}

Write-Host ""

# ============================================
# Test 5: Validation Test (Missing Required Fields)
# ============================================
Write-Host "Test 5: Validation Test (Missing Required Fields)" -ForegroundColor Yellow

$invalidViolation = @{
    vehicleNumber = ""  # Missing required
    driverName = ""     # Missing required
    violationType = ""  # Missing required
    fineAmount = 0
    location = ""       # Missing required
    isDraft = $false
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$BackendUrl/api/violations" -Method POST -Body $invalidViolation -Headers $headers
    Write-Host "❌ Validation failed - server accepted invalid data" -ForegroundColor Red
    $testResults += @{Test = "Validation"; Status = "FAIL"}
} catch {
    Write-Host "✅ Validation working - server rejected invalid data" -ForegroundColor Green
    Write-Host "   Expected error received" -ForegroundColor Cyan
    $testResults += @{Test = "Validation"; Status = "PASS"}
}

Write-Host ""

# ============================================
# Test 6: GPS Location Data
# ============================================
Write-Host "Test 6: Create Violation with GPS Data" -ForegroundColor Yellow

$gpsViolation = @{
    vehicleNumber = "DL08CA5678"
    vehicleType = "car"
    driverName = "GPS Test User"
    violationType = "illegal_parking"
    fineAmount = 1000
    location = "Connaught Place, Delhi"
    violationDateTime = (Get-Date).ToString("yyyy-MM-ddTHH:mm")
    description = "Testing GPS coordinates capture"
    latitude = 28.632244
    longitude = 77.219734
    gpsLocation = "28.632244,77.219734"
    isDraft = $false
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$BackendUrl/api/violations" -Method POST -Body $gpsViolation -Headers $headers
    Write-Host "✅ Violation with GPS data created successfully" -ForegroundColor Green
    Write-Host "   Ticket Number: $($response.violation.ticketNumber)" -ForegroundColor Cyan
    Write-Host "   GPS Location: $($response.violation.gpsLocation)" -ForegroundColor Cyan
    Write-Host "   Latitude: $($response.violation.latitude)" -ForegroundColor Cyan
    Write-Host "   Longitude: $($response.violation.longitude)" -ForegroundColor Cyan
    $testResults += @{Test = "GPS Data"; Status = "PASS"}
} catch {
    Write-Host "❌ Failed to create violation with GPS data" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    $testResults += @{Test = "GPS Data"; Status = "FAIL"}
}

Write-Host ""

# ============================================
# Test Summary
# ============================================
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "📊 TEST SUMMARY" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

$passCount = ($testResults | Where-Object { $_.Status -eq "PASS" }).Count
$failCount = ($testResults | Where-Object { $_.Status -eq "FAIL" }).Count
$totalTests = $testResults.Count

Write-Host "Total Tests: $totalTests" -ForegroundColor White
Write-Host "Passed: $passCount" -ForegroundColor Green
Write-Host "Failed: $failCount" -ForegroundColor Red

Write-Host "`nDetailed Results:" -ForegroundColor Yellow
$testResults | ForEach-Object {
    $icon = if ($_.Status -eq "PASS") { "✅" } else { "❌" }
    $color = if ($_.Status -eq "PASS") { "Green" } else { "Red" }
    Write-Host "  $icon $($_.Test): $($_.Status)" -ForegroundColor $color
}

Write-Host ""

# ============================================
# MongoDB Verification Instructions
# ============================================
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "🗄️  MONGODB VERIFICATION" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "Run these commands in MongoDB shell to verify:" -ForegroundColor Yellow
Write-Host @"

# Connect to MongoDB
mongosh

# Check databases
show dbs

# Check Testing database (users only)
use Testing
show collections
db.users.countDocuments()

# Check ViolationsDB (violations only)
use ViolationsDB
show collections
db.violations.countDocuments()

# View recent violations
db.violations.find().sort({createdAt: -1}).limit(5).pretty()

# Check for drafts
db.violations.find({isDraft: true}).pretty()

# Check violations by status
db.violations.find({status: "PENDING"}).count()
db.violations.find({status: "DRAFT"}).count()

"@ -ForegroundColor Cyan

Write-Host ""

# ============================================
# Final Status
# ============================================
if ($failCount -eq 0) {
    Write-Host "🎉 All tests passed! Violation workflow is working correctly." -ForegroundColor Green
    exit 0
} else {
    Write-Host "⚠️  Some tests failed. Please review the errors above." -ForegroundColor Yellow
    exit 1
}
