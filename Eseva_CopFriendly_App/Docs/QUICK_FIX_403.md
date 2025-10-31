# 🔧 Fix 403 Forbidden Error - Login Required

## Problem
- **403 Error:** Backend rejects requests because you're not authenticated
- **Cause:** Using mock login instead of real backend authentication
- **ViolationsDB Not Created:** Database only gets created when first violation is saved (after successful authentication)

## ✅ Solution: Login via Backend First

### Method 1: Use Test Page (RECOMMENDED)
1. **Open test page:**
   ```
   E:\CopFriendlyApp\test-violation-form.html
   ```
   (Double-click the file)

2. **Login with admin credentials:**
   - Click "Login as Admin" button
   - OR manually enter:
     - Username: `admin`
     - Password: `admin123`
   - Click "Login"

3. **Verify login success:**
   - You should see green success message
   - Token will be saved in localStorage

4. **Fill test data and submit:**
   - Click "Fill Test Data"
   - Click "Submit Violation"
   - Should get success response

### Method 2: Login via Frontend App
1. **Open your frontend app:**
   ```
   http://localhost:5173/login
   ```

2. **Login with backend admin account:**
   - Username: `admin`
   - Password: `admin123`

3. **Check browser console:**
   - Open DevTools (F12)
   - Console should show "Backend response:" with token
   - Check Application > Local Storage > token

4. **Navigate to Create Violation:**
   ```
   http://localhost:5173/violations/create
   ```

5. **Fill and submit form**

## Verify Authentication

### Check if you have valid token:
1. Open browser DevTools (F12)
2. Go to **Application** tab > **Local Storage**
3. Check `token` value:
   - ❌ **Mock token:** `token-1-1729245600123` (won't work with backend)
   - ✅ **Real JWT:** `eyJhbGciOiJIUzI1NiJ9.eyJzdWI...` (long string)

## Create Admin User (If Needed)

If admin doesn't exist in backend, create one:

### Method 1: Using MongoDB Directly
```javascript
// In MongoDB Shell or Compass
use Testing

db.users.insertOne({
  username: "admin",
  password: "$2a$10$YourBCryptHashHere",  // "admin123" hashed
  fullName: "System Administrator",
  email: "admin@copfriendly.com",
  role: "admin",
  status: "approved",
  loginRequested: false,
  createdAt: new Date(),
  updatedAt: new Date()
})
```

### Method 2: Register via API
```powershell
# Register admin user
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" `
  -Method POST `
  -ContentType "application/json" `
  -Body (@{
    username = "admin"
    password = "admin123"
    confirmPassword = "admin123"
    fullName = "System Administrator"
    email = "admin@copfriendly.com"
    role = "admin"
  } | ConvertTo-Json)
```

## Test Workflow

1. **Backend running?**
   ```
   http://localhost:8080
   ```
   Should show: "Whitelabel Error Page" (means Spring Boot is running)

2. **Login:**
   ```
   POST http://localhost:8080/api/auth/login
   {
     "username": "admin",
     "password": "admin123"
   }
   ```
   Should return: `{ "token": "eyJ...", "username": "admin", ... }`

3. **Create violation (with token):**
   ```
   POST http://localhost:8080/api/violations
   Authorization: Bearer eyJ...
   {
     "vehicleNumber": "MH01AB1234",
     "vehicleType": "car",
     "driverName": "Test Driver",
     "violationType": "speeding",
     "fineAmount": 500,
     "location": "MG Road",
     "violationDateTime": "2025-10-18T10:30:00"
   }
   ```

4. **Check MongoDB:**
   ```
   use ViolationsDB
   db.violations.find().pretty()
   ```

## Common Errors & Solutions

### ❌ 403 Forbidden
- **Cause:** No token or invalid token
- **Fix:** Login first to get valid JWT token

### ❌ 401 Unauthorized
- **Cause:** Token expired
- **Fix:** Login again to get fresh token

### ❌ Token starts with "token-1-"
- **Cause:** Using mock auth instead of backend
- **Fix:** Clear localStorage and login via backend

### ❌ ViolationsDB not appearing
- **Cause:** No violations created yet
- **Fix:** Successfully create first violation (after fixing 403)

## Quick Test Command

```powershell
# Test backend login
$loginResp = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"username":"admin","password":"admin123"}'

Write-Host "Token: $($loginResp.token)"

# Test create violation
$headers = @{ Authorization = "Bearer $($loginResp.token)" }
Invoke-RestMethod -Uri "http://localhost:8080/api/violations" `
  -Method POST `
  -ContentType "application/json" `
  -Headers $headers `
  -Body (@{
    vehicleNumber = "TEST123"
    vehicleType = "car"
    driverName = "Test Driver"
    violationType = "speeding"
    fineAmount = 500
    location = "Test Location"
    violationDateTime = "2025-10-18T10:30:00"
  } | ConvertTo-Json)
```

## Next Steps After Login

1. ✅ Login successful with backend
2. ✅ Token stored in localStorage
3. ✅ Create violation via form
4. ✅ Check ViolationsDB in MongoDB (should now exist!)
5. ✅ Verify violation document has correct structure

---

**Quick Start:** Double-click `test-violation-form.html` → Click "Login as Admin" → Fill Test Data → Submit!
