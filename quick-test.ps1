# Quick Setup and Test Script
# This will check/create admin user and test violation creation

Write-Host "=== CopFriendly Backend Test ===" -ForegroundColor Cyan
Write-Host ""

# 1. Check if backend is running
Write-Host "1. Checking backend status..." -ForegroundColor Yellow
try {
    $backendCheck = Invoke-WebRequest -Uri "http://localhost:8080" -ErrorAction Stop
    Write-Host "   ✅ Backend is running on port 8080" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Backend is NOT running!" -ForegroundColor Red
    Write-Host "   Please start backend first: cd backend && mvn spring-boot:run" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# 2. Try to login
Write-Host "2. Testing admin login..." -ForegroundColor Yellow
try {
    $loginBody = @{
        username = "admin"
        password = "admin123"
    } | ConvertTo-Json

    $loginResp = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginBody `
        -ErrorAction Stop

    $token = $loginResp.token
    Write-Host "   ✅ Login successful!" -ForegroundColor Green
    Write-Host "   Username: $($loginResp.username)" -ForegroundColor Cyan
    Write-Host "   Role: $($loginResp.role)" -ForegroundColor Cyan
    Write-Host "   Token: $($token.Substring(0, 30))..." -ForegroundColor Gray
} catch {
    Write-Host "   ❌ Login failed!" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    
    # Try to register admin user
    Write-Host ""
    Write-Host "3. Attempting to register admin user..." -ForegroundColor Yellow
    try {
        $registerBody = @{
            username = "admin"
            password = "admin123"
            confirmPassword = "admin123"
            fullName = "System Administrator"
            email = "admin@copfriendly.com"
            firstName = "System"
            lastName = "Administrator"
            phone = "1234567890"
            dateOfBirth = "1990-01-01"
            role = "admin"
        } | ConvertTo-Json

        $registerResp = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" `
            -Method POST `
            -ContentType "application/json" `
            -Body $registerBody `
            -ErrorAction Stop

        Write-Host "   ✅ Admin user registered!" -ForegroundColor Green
        
        # Try login again
        Write-Host ""
        Write-Host "4. Logging in with new admin account..." -ForegroundColor Yellow
        $loginResp = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
            -Method POST `
            -ContentType "application/json" `
            -Body $loginBody `
            -ErrorAction Stop
        
        $token = $loginResp.token
        Write-Host "   ✅ Login successful!" -ForegroundColor Green
    } catch {
        Write-Host "   ❌ Registration failed!" -ForegroundColor Red
        Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host ""
        Write-Host "MANUAL STEPS REQUIRED:" -ForegroundColor Yellow
        Write-Host "1. Check if user exists in MongoDB (Testing.users collection)"
        Write-Host "2. If user exists, verify password is 'admin123'"
        Write-Host "3. If loginRequested=true, approve the user first"
        exit 1
    }
}

Write-Host ""

# 3. Test creating a violation
Write-Host "5. Testing violation creation..." -ForegroundColor Yellow
try {
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }

    $violationBody = @{
        vehicleNumber = "TEST" + (Get-Random -Maximum 9999)
        vehicleType = "car"
        vehicleModel = "Test Model"
        vehicleColor = "Blue"
        driverName = "Test Driver"
        licenseNumber = "DL12345"
        driverPhoneNumber = "9876543210"
        driverAddress = "Test Address"
        violationType = "speeding"
        fineAmount = 500
        location = "Test Location"
        violationDateTime = (Get-Date -Format "yyyy-MM-ddTHH:mm:ss")
        description = "Test violation created by script"
        latitude = 12.9716
        longitude = 77.5946
    } | ConvertTo-Json

    $violationResp = Invoke-RestMethod -Uri "http://localhost:8080/api/violations" `
        -Method POST `
        -Headers $headers `
        -Body $violationBody `
        -ErrorAction Stop

    Write-Host "   ✅ Violation created successfully!" -ForegroundColor Green
    Write-Host "   Ticket Number: $($violationResp.ticketNumber)" -ForegroundColor Cyan
    Write-Host "   Violation ID: $($violationResp.id)" -ForegroundColor Cyan
    Write-Host "   Status: $($violationResp.status)" -ForegroundColor Cyan
    Write-Host "   Vehicle: $($violationResp.vehicleNumber)" -ForegroundColor Cyan
} catch {
    Write-Host "   ❌ Violation creation failed!" -ForegroundColor Red
    Write-Host "   Status Code: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    
    if ($_.Exception.Response.StatusCode.value__ -eq 403) {
        Write-Host ""
        Write-Host "   403 FORBIDDEN - Token is invalid or user not approved" -ForegroundColor Yellow
        Write-Host "   Check if loginRequested=true in MongoDB (Testing.users)" -ForegroundColor Yellow
    }
    exit 1
}

Write-Host ""

# 4. Verify in MongoDB
Write-Host "6. Checking MongoDB..." -ForegroundColor Yellow
Write-Host "   Run this command to verify:" -ForegroundColor Cyan
Write-Host "   mongosh" -ForegroundColor Gray
Write-Host "   use ViolationsDB" -ForegroundColor Gray
Write-Host "   db.violations.find().pretty()" -ForegroundColor Gray

Write-Host ""
Write-Host "=== ✅ ALL TESTS PASSED ===" -ForegroundColor Green
Write-Host ""
Write-Host "Your token (save this):" -ForegroundColor Yellow
Write-Host $token -ForegroundColor Gray
Write-Host ""
Write-Host "You can now:" -ForegroundColor Cyan
Write-Host "1. Open test-violation-form.html and login" -ForegroundColor White
Write-Host "2. Use the frontend app at http://localhost:5173" -ForegroundColor White
Write-Host "3. Check ViolationsDB in MongoDB Compass" -ForegroundColor White
