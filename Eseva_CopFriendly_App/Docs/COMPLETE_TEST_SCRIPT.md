# 🧪 Complete Test Script - User Approval System

## Current System Status

✅ **Database Verified**: MongoDB has correct structure
✅ **Existing User**: police@gov.in (status: approved, active: true)
✅ **Backend Code**: Updated with approval logic
✅ **Frontend Code**: Updated with admin dashboard
✅ **Ready to Test**: System is fully operational

---

## 🎯 Test Plan

### Test 1: Login with Existing Approved User ✅

**Existing User in Database:**
```
Username: police
Password: Qwerty@123
Email: police@gov.in
Status: approved ✅
Active: true ✅
Role: traffic_sergeant
```

**Steps:**
1. Go to: http://localhost:5173/login
2. Enter credentials:
   - Username: `police`
   - Password: `Qwerty@123`
   - Role: `Traffic Sergeant`
3. Click "Sign in"

**Expected Result:**
✅ Login successful
✅ Redirected to Traffic Sergeant Dashboard
✅ JWT token stored in localStorage

---

### Test 2: Register New User (Will be Pending) 🆕

**Steps:**
1. Go to: http://localhost:5173/register (or click Register)
2. Fill registration form:
   ```
   Username: testuser123
   Email: testuser@test.com
   Password: Test@1234
   Confirm Password: Test@1234
   Full Name: Test User
   First Name: Test
   Last Name: User
   Phone: 9876543210
   Date of Birth: 1990-01-01
   Role: Traffic Sergeant
   ```
3. Click "Register"

**Expected Result:**
✅ Registration successful
✅ Message: "Account created successfully"
✅ User stored in database with:
   - `status: "pending"`
   - `active: false`

**Verify in MongoDB:**
```javascript
db.users.findOne({ username: "testuser123" })
// Should show status: "pending", active: false
```

---

### Test 3: Try to Login as Pending User ❌

**Steps:**
1. Go to: http://localhost:5173/login
2. Enter new user credentials:
   - Username: `testuser123`
   - Password: `Test@1234`
   - Role: `Traffic Sergeant`
3. Click "Sign in"

**Expected Result:**
❌ Login FAILS
❌ Error message displayed:
   "Your account is pending admin approval. Please wait for approval before logging in."

**Backend Response:**
```json
{
  "error": "Your account is pending admin approval. Please wait for approval before logging in."
}
```

---

### Test 4: Admin Views Pending Users 👨‍💼

**Steps:**
1. Go to: http://localhost:5173/login
2. Login as admin:
   - Username: `admin`
   - Password: `admin123`
   - Role: `System Administrator`
3. You'll be redirected to Admin Dashboard

**Expected Result:**
✅ Admin Dashboard loads
✅ Statistics show:
   - Pending Approvals: 1 (or more)
   - Active Users: count of approved users
   - Total Users: all users
✅ "Pending Approvals" filter is selected by default
✅ New user "testuser123" appears in the list with status "PENDING"

---

### Test 5: Admin Approves User ✅

**Steps:**
1. On Admin Dashboard
2. Find "testuser123" in the pending users list
3. Click "✅ Approve" button
4. Confirm approval in the dialog

**Expected Result:**
✅ Success message: "User Approved!"
✅ User disappears from pending list
✅ Statistics update automatically
✅ Activity log shows "User Approved: testuser123..."

**Verify in Database:**
```javascript
db.users.findOne({ username: "testuser123" })
// Should show:
// status: "approved"
// active: true
// updatedAt: recent timestamp
```

**Check Backend API:**
```bash
# If you want to verify the API call
POST http://localhost:8080/api/auth/users/{userId}/approve
Authorization: Bearer {admin_token}

# Response should be:
{
  "message": "User approved"
}
```

---

### Test 6: Approved User Can Now Login ✅

**Steps:**
1. Logout from admin (or use incognito window)
2. Go to: http://localhost:5173/login
3. Enter the approved user credentials:
   - Username: `testuser123`
   - Password: `Test@1234`
   - Role: `Traffic Sergeant`
4. Click "Sign in"

**Expected Result:**
✅ Login SUCCESSFUL
✅ JWT token generated
✅ Redirected to Traffic Sergeant Dashboard
✅ User can access their dashboard

---

### Test 7: Admin Rejects User ❌

**Steps:**
1. Register another new user: `rejecteduser`
2. Login as admin
3. Find "rejecteduser" in pending list
4. Click "❌ Reject" button
5. Enter rejection reason: "Invalid credentials"
6. Confirm rejection

**Expected Result:**
✅ Success message: "User Rejected!"
✅ User status changed to "rejected"
✅ Activity log updated

**Verify in Database:**
```javascript
db.users.findOne({ username: "rejecteduser" })
// Should show:
// status: "rejected"
// active: false
```

**Try to login as rejected user:**
❌ Login FAILS
❌ Error: "Your account has been rejected by the administrator. Please contact support."

---

### Test 8: Admin Suspends Active User 🚫

**Steps:**
1. Login as admin
2. Click "Approved Users" filter
3. Find an approved user (e.g., "testuser123")
4. Click "🚫 Suspend" button
5. Enter suspension reason: "Policy violation"
6. Confirm suspension

**Expected Result:**
✅ Success message: "User Suspended!"
✅ User status changed to "suspended"
✅ User removed from approved list

**Verify Suspended User Cannot Login:**
1. Try to login as suspended user
2. ❌ Error: "Your account has been suspended. Please contact the administrator."

**Verify in Database:**
```javascript
db.users.findOne({ username: "testuser123" })
// Should show:
// status: "suspended"
// active: false
```

---

### Test 9: Admin Reactivates Suspended User ✅

**Steps:**
1. Login as admin
2. Click "Suspended Users" filter
3. Find the suspended user
4. Click "✅ Reactivate" button
5. Confirm reactivation

**Expected Result:**
✅ Success message: "User Reactivated!"
✅ User status changed back to "approved"
✅ User can login again

---

### Test 10: Admin Changes User Role 🔄

**Steps:**
1. Login as admin
2. Find any approved user
3. Click "🔄 Change Role" button
4. Select new role (e.g., "2" for Traffic Inspector)
5. Confirm role change

**Expected Result:**
✅ Success message: "Role Updated!"
✅ User role changed in database
✅ Activity log updated

---

### Test 11: Admin Deletes User 🗑️

**Steps:**
1. Login as admin
2. Find a test user
3. Click "🗑️ Delete" button
4. Confirm deletion
5. Type "DELETE" to double-confirm

**Expected Result:**
✅ Success message: "User Deleted!"
✅ User removed from database
✅ User cannot login anymore

---

### Test 12: Search and Filter 🔍

**Steps:**
1. Login as admin
2. Test search box:
   - Search by username: "test"
   - Search by email: "@test.com"
3. Test role filter:
   - Select "Traffic Sergeant"
   - Select "Traffic Inspector"
4. Test status filters:
   - Click "Pending Approvals"
   - Click "Approved Users"
   - Click "Rejected Users"
   - Click "Suspended Users"
   - Click "All Users"
5. Test "Clear Filters" button

**Expected Result:**
✅ Search filters users correctly
✅ Role filter works
✅ Status filters work
✅ Clear filters resets everything

---

## 🔍 API Testing (Optional)

### Test Registration API
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "apitest",
    "password": "Test@1234",
    "confirmPassword": "Test@1234",
    "email": "apitest@test.com",
    "fullName": "API Test User",
    "firstName": "API",
    "lastName": "Test",
    "phone": "9999999999",
    "dateOfBirth": "1990-01-01",
    "role": "traffic_sergeant"
  }'
```

**Expected Response:**
```json
{
  "token": null,
  "username": "apitest",
  "email": "apitest@test.com",
  "fullName": "API Test User",
  "role": "traffic_sergeant"
}
```

### Test Login API (Pending User)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "apitest",
    "password": "Test@1234"
  }'
```

**Expected Response:**
```json
{
  "error": "Your account is pending admin approval. Please wait for approval before logging in."
}
```

### Test Login API (Approved User)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "police",
    "password": "Qwerty@123"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "police",
  "email": "police@gov.in",
  "fullName": "police officer",
  "role": "traffic_sergeant"
}
```

---

## ✅ Test Results Checklist

After completing all tests, verify:

- [ ] Existing approved user can login (Test 1)
- [ ] New user registration creates pending user (Test 2)
- [ ] Pending user cannot login (Test 3)
- [ ] Admin can view pending users (Test 4)
- [ ] Admin can approve users (Test 5)
- [ ] Approved user can login (Test 6)
- [ ] Admin can reject users (Test 7)
- [ ] Rejected user cannot login (Test 7)
- [ ] Admin can suspend users (Test 8)
- [ ] Suspended user cannot login (Test 8)
- [ ] Admin can reactivate users (Test 9)
- [ ] Admin can change user roles (Test 10)
- [ ] Admin can delete users (Test 11)
- [ ] Search and filters work (Test 12)

---

## 🎉 All Tests Passing = System Working Perfectly!

If all tests pass, your user approval system is **production ready**! 🚀

---

**Test Date**: October 17, 2025
**Tester**: You
**System**: CopFriendly ESeva
**Status**: Ready for Testing
