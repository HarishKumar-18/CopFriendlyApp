# Test login script
$body = @{
    username = "police"
    password = "Qwerty@123"
} | ConvertTo-Json

Write-Host "Testing login with username: police"
Write-Host "Request body: $body"

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $body
    Write-Host "✅ Login successful!" -ForegroundColor Green
    Write-Host ($response | ConvertTo-Json -Depth 10)
} catch {
    Write-Host "❌ Login failed!" -ForegroundColor Red
    Write-Host "Status Code:" $_.Exception.Response.StatusCode.value__
    Write-Host "Error:" $_.Exception.Message
    if ($_.ErrorDetails) {
        Write-Host "Details:" $_.ErrorDetails.Message
    }
}
