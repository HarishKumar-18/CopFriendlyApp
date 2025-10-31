# Quick backend API test script

Write-Host "=== Testing Backend APIs ===" -ForegroundColor Green

# 1. Register a test user
Write-Host "`n1. Registering test user..." -ForegroundColor Yellow
$registerBody = @{
    username = "testuser"
    email = "test@example.com"
    password = "test123"
    fullName = "Test User"
    firstName = "Test"
    lastName = "User"
    phone = "1234567890"
    dateOfBirth = "1990-01-01"
    role = "citizen"
    confirmPassword = "test123"
} | ConvertTo-Json

try {
    $registerResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -Body $registerBody -ContentType "application/json"
    Write-Host "✓ Registration successful" -ForegroundColor Green
    Write-Host "  Message: $($registerResponse.message)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Registration failed: $_" -ForegroundColor Red
}

# 2. Try to login (should fail - pending)
Write-Host "`n2. Attempting login with pending user..." -ForegroundColor Yellow
$loginBody = @{
    username = "testuser"
    password = "test123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    Write-Host "✗ Login should have failed but succeeded" -ForegroundColor Red
} catch {
    $errorResponse = $_.ErrorDetails.Message | ConvertFrom-Json
    Write-Host "✓ Login blocked as expected" -ForegroundColor Green
    Write-Host "  Message: $($errorResponse.message)" -ForegroundColor Cyan
}

# 3. Login as admin
Write-Host "`n3. Logging in as admin..." -ForegroundColor Yellow
$adminLoginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $adminLoginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $adminLoginBody -ContentType "application/json"
    $token = $adminLoginResponse.token
    Write-Host "✓ Admin login successful" -ForegroundColor Green
    Write-Host "  Token: $($token.Substring(0, 20))..." -ForegroundColor Cyan
    
    # 4. Check pending users
    Write-Host "`n4. Fetching pending users..." -ForegroundColor Yellow
    $headers = @{
        "Authorization" = "Bearer $token"
    }
    $pendingUsers = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/users/pending" -Method GET -Headers $headers
    Write-Host "✓ Found $($pendingUsers.Count) pending user(s)" -ForegroundColor Green
    
} catch {
    Write-Host "✗ Admin login failed: $_" -ForegroundColor Red
}

Write-Host "`n=== Test Complete ===" -ForegroundColor Green
