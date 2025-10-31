# Script: change_username_pranav.ps1
# Logs in as admin, finds user with username 'pranav.muthyala', and updates their username to their first name (sanitized)

$baseUrl = 'http://localhost:8080'
$adminCreds = @{ username = 'admin'; password = 'admin123' }

try {
    Write-Output "Logging in as admin..."
    $loginResp = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method Post -Body ($adminCreds | ConvertTo-Json) -ContentType 'application/json' -ErrorAction Stop
} catch {
    Write-Error "Login failed: $($_.Exception.Message)"
    if ($_.Exception.Response) { $_.Exception.Response.Content.ReadAsStringAsync().Result | Write-Output }
    exit 1
}
$token = $loginResp.token
if (-not $token) { Write-Error "No token returned from login. Unable to continue."; exit 1 }
Write-Output "Got token: $($token.Substring(0,20))..."
$headers = @{ Authorization = "Bearer $token" }

# Fetch users
try {
    $resp = Invoke-RestMethod -Uri "$baseUrl/api/users" -Method Get -Headers $headers -ErrorAction Stop
} catch {
    Write-Error "Failed to fetch users: $($_.Exception.Message)"
    if ($_.Exception.Response) { $_.Exception.Response.Content.ReadAsStringAsync().Result | Write-Output }
    exit 1
}

# Normalize possible response shapes
if ($null -eq $resp) { Write-Error 'Empty response fetching users'; exit 1 }
$users = if ($resp.users) { $resp.users } else { $resp }

$target = $users | Where-Object { $_.username -eq 'pranav.muthyala' }
if (-not $target) { Write-Error "User with username 'pranav.muthyala' not found"; exit 1 }
$u = $target[0]
$id = $u.id
$firstName = $u.firstName
if (-not $firstName) { Write-Error "User has no firstName"; exit 1 }
$new = ($firstName -replace '[^A-Za-z0-9]', '').ToLower().Trim()
if ($new.Length -lt 2) { $new = (($u.fullName -replace '[^A-Za-z0-9]','').ToLower()) }
if ($new.Length -lt 2) { $new = 'user' + ((New-Guid).Guid.Substring(0,4)) }

Write-Output "Updating user id $id -> username: $new"
$body = @{ username = $new }

try {
    $updated = Invoke-RestMethod -Uri "$baseUrl/api/users/$id" -Method Put -Headers $headers -Body ($body | ConvertTo-Json) -ContentType 'application/json' -ErrorAction Stop
    Write-Output 'Update successful. Server returned:'
    $updated | ConvertTo-Json -Depth 5
} catch {
    Write-Error "Update failed: $($_.Exception.Message)"
    if ($_.Exception.Response) { $_.Exception.Response.Content.ReadAsStringAsync().Result | Write-Output }
    exit 1
}
