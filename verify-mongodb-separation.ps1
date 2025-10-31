# MongoDB Database Separation Verification Script

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "🗄️  MongoDB Database Separation Test" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Test if mongosh is available
try {
    $null = Get-Command mongosh -ErrorAction Stop
} catch {
    Write-Host "❌ mongosh not found. Please install MongoDB Shell." -ForegroundColor Red
    Write-Host "   Download from: https://www.mongodb.com/try/download/shell" -ForegroundColor Yellow
    exit 1
}

Write-Host "Connecting to MongoDB at localhost:27017...`n" -ForegroundColor Yellow

# Check databases
Write-Host "1️⃣  Checking databases..." -ForegroundColor Yellow
$databases = mongosh --quiet --eval "db.adminCommand('listDatabases').databases.map(d => d.name).join(',')"

if ($databases -match "Testing" -and $databases -match "ViolationsDB") {
    Write-Host "✅ Both databases exist:" -ForegroundColor Green
    Write-Host "   - Testing" -ForegroundColor Cyan
    Write-Host "   - ViolationsDB" -ForegroundColor Cyan
} else {
    Write-Host "⚠️  Missing databases!" -ForegroundColor Yellow
    if ($databases -notmatch "Testing") {
        Write-Host "   ❌ Testing database not found" -ForegroundColor Red
    }
    if ($databases -notmatch "ViolationsDB") {
        Write-Host "   ❌ ViolationsDB database not found" -ForegroundColor Red
    }
}

Write-Host ""

# Check Testing database (should have users)
Write-Host "2️⃣  Checking Testing database (Users)..." -ForegroundColor Yellow
$userCount = mongosh --quiet Testing --eval "db.users.countDocuments()"
Write-Host "   Users count: $userCount" -ForegroundColor Cyan

if ([int]$userCount -gt 0) {
    Write-Host "   ✅ Users collection exists with data" -ForegroundColor Green
    
    # Show sample user
    Write-Host "`n   Sample user:" -ForegroundColor White
    mongosh --quiet Testing --eval "db.users.findOne({}, {username: 1, email: 1, role: 1, status: 1})" | Write-Host
} else {
    Write-Host "   ⚠️  No users found in Testing database" -ForegroundColor Yellow
}

# Check if violations exist in Testing (should be 0)
$violationsInTesting = mongosh --quiet Testing --eval "db.violations ? db.violations.countDocuments() : 0"
if ([int]$violationsInTesting -eq 0) {
    Write-Host "   ✅ No violations in Testing database (correct)" -ForegroundColor Green
} else {
    Write-Host "   ❌ WARNING: Found $violationsInTesting violations in Testing database (should be 0)" -ForegroundColor Red
}

Write-Host ""

# Check ViolationsDB database (should have violations)
Write-Host "3️⃣  Checking ViolationsDB database (Violations)..." -ForegroundColor Yellow
$violationCount = mongosh --quiet ViolationsDB --eval "db.violations.countDocuments()"
Write-Host "   Violations count: $violationCount" -ForegroundColor Cyan

if ([int]$violationCount -gt 0) {
    Write-Host "   ✅ Violations collection exists with data" -ForegroundColor Green
    
    # Show sample violation
    Write-Host "`n   Sample violation:" -ForegroundColor White
    mongosh --quiet ViolationsDB --eval "db.violations.findOne({}, {ticketNumber: 1, vehicleNumber: 1, violationType: 1, status: 1, isDraft: 1})" | Write-Host
    
    # Count by status
    Write-Host "`n   Violations by status:" -ForegroundColor White
    $pending = mongosh --quiet ViolationsDB --eval "db.violations.countDocuments({status: 'PENDING'})"
    $draft = mongosh --quiet ViolationsDB --eval "db.violations.countDocuments({status: 'DRAFT'})"
    $approved = mongosh --quiet ViolationsDB --eval "db.violations.countDocuments({status: 'APPROVED'})"
    
    Write-Host "   - PENDING: $pending" -ForegroundColor Cyan
    Write-Host "   - DRAFT: $draft" -ForegroundColor Cyan
    Write-Host "   - APPROVED: $approved" -ForegroundColor Cyan
    
} else {
    Write-Host "   ⚠️  No violations found in ViolationsDB" -ForegroundColor Yellow
}

# Check if users exist in ViolationsDB (should be 0)
$usersInViolations = mongosh --quiet ViolationsDB --eval "db.users ? db.users.countDocuments() : 0"
if ([int]$usersInViolations -eq 0) {
    Write-Host "   ✅ No users in ViolationsDB (correct)" -ForegroundColor Green
} else {
    Write-Host "   ❌ WARNING: Found $usersInViolations users in ViolationsDB (should be 0)" -ForegroundColor Red
}

Write-Host ""

# Summary
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "📊 VERIFICATION SUMMARY" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

$issues = @()

if ($databases -notmatch "Testing") { $issues += "Testing database missing" }
if ($databases -notmatch "ViolationsDB") { $issues += "ViolationsDB database missing" }
if ([int]$violationsInTesting -gt 0) { $issues += "Violations found in Testing database" }
if ([int]$usersInViolations -gt 0) { $issues += "Users found in ViolationsDB" }

if ($issues.Count -eq 0) {
    Write-Host "✅ Database separation is CORRECT!" -ForegroundColor Green
    Write-Host "   - Users stored in Testing database" -ForegroundColor Green
    Write-Host "   - Violations stored in ViolationsDB database" -ForegroundColor Green
    Write-Host "   - No cross-contamination detected" -ForegroundColor Green
} else {
    Write-Host "⚠️  Issues detected:" -ForegroundColor Yellow
    $issues | ForEach-Object {
        Write-Host "   - $_" -ForegroundColor Red
    }
}

Write-Host ""

# Show collection details
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "📋 DETAILED VIEW" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "Testing Database Collections:" -ForegroundColor Yellow
mongosh --quiet Testing --eval "db.getCollectionNames()" | Write-Host

Write-Host "`nViolationsDB Database Collections:" -ForegroundColor Yellow
mongosh --quiet ViolationsDB --eval "db.getCollectionNames()" | Write-Host

Write-Host ""

# Recent violations
if ([int]$violationCount -gt 0) {
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "📄 RECENT VIOLATIONS" -ForegroundColor Cyan
    Write-Host "========================================`n" -ForegroundColor Cyan
    
    mongosh --quiet ViolationsDB --eval @"
    db.violations.find().sort({createdAt: -1}).limit(5).forEach(v => {
        print('Ticket: ' + v.ticketNumber);
        print('Vehicle: ' + v.vehicleNumber + ' (' + v.vehicleType + ')');
        print('Driver: ' + v.driverName);
        print('Type: ' + v.violationType + ' | Fine: ₹' + v.fineAmount);
        print('Status: ' + v.status + ' | Draft: ' + v.isDraft);
        print('Location: ' + v.location);
        if (v.gpsLocation) print('GPS: ' + v.gpsLocation);
        print('Created: ' + v.createdAt);
        print('---');
    });
"@ | Write-Host
}

Write-Host "`n✨ Verification complete!`n" -ForegroundColor Green
