# 🚀 Quick Start Guide - User Approval System

## ⚡ Start the Application

### 1. Start Backend (Terminal 1)
```bash
cd e:\CopFriendlyApp\backend
mvn spring-boot:run
```
**Backend will start on**: http://localhost:8080

### 2. Start Frontend (Terminal 2)
```bash
cd e:\CopFriendlyApp\frontend
npm run dev
```
**Frontend will start on**: http://localhost:5173

## 🧪 Quick Test Flow

### Test 1: Register New User → Admin Approval → Login
```
Step 1: Register New User
- Go to: http://localhost:5173
- Click "Register" (or go to /register)
- Fill form:
  Username: testuser1
  Email: test@example.com
  Password: Test@123
  Role: Traffic Sergeant
- Submit ✅
- Expected: "Account created successfully"

Step 2: Try to Login (Should Fail)
- Go to: http://localhost:5173/login
- Enter credentials: testuser1 / Test@123
- Click "Sign in"
- Expected: ❌ "Your account is pending admin approval"

Step 3: Admin Approves User
- Login as admin:
  Username: admin
  Password: admin123
  Role: System Administrator
- You'll see Admin Dashboard
- Click "Pending Approvals" filter (should be default)
- Find "testuser1" in the list
- Click "✅ Approve" button
- Confirm approval
- Expected: User status changes to "Approved"

Step 4: User Can Now Login
- Logout from admin
- Login as: testuser1 / Test@123
- Expected: ✅ Login successful → Redirected to dashboard
```

## 🎯 Key Features to Test

### Admin Dashboard Features
```
1. Statistics Cards
   - View pending approvals count
   - View active users count
   - View suspended users count
   - View total users count

2. Quick Filters
   - Click "Pending Approvals" → See only pending users
   - Click "Approved Users" → See only approved users
   - Click "Rejected Users" → See only rejected users
   - Click "Suspended Users" → See only suspended users
   - Click "All Users" → See everyone

3. Search
   - Type in search box to filter by:
     * Name
     * Email
     * Username
     * Badge Number

4. User Actions (on each user row)
   ✅ Approve - Approve pending user
   ❌ Reject - Reject pending user (with reason)
   🚫 Suspend - Suspend active user (with reason)
   ✅ Reactivate - Reactivate suspended user
   🔄 Change Role - Change user's role
   👁️ View - View full user details
   🗑️ Delete - Permanently delete user

5. Activity Log
   - See recent admin actions
   - Track who was approved/rejected/suspended
```

## 📊 User Status Flow

```
NEW USER REGISTRATION
        ↓
   [PENDING]  ← Default status
        ↓
Admin Decision:
        ├─→ [APPROVED] ✅ → User can login
        ├─→ [REJECTED] ❌ → User cannot login
        └─→ [SUSPENDED] 🚫 → User cannot login
                ↓
        [REACTIVATED] → Back to [APPROVED]
```

## 🔑 Demo Accounts

### Admin Account (Pre-approved)
```
Username: admin
Password: admin123
Role: System Administrator
Status: APPROVED ✅
```

### Test User Accounts (Pre-approved)
```
Traffic Sergeant:
Username: sergeant1
Password: password123
Status: APPROVED ✅

Traffic Inspector:
Username: inspector1
Password: password123
Status: APPROVED ✅

Central Team:
Username: central1
Password: password123
Status: APPROVED ✅

Medical Team:
Username: medic1
Password: password123
Status: APPROVED ✅
```

## 🛠️ Troubleshooting

### Backend not starting?
```bash
# Check if MongoDB is running (if using MongoDB)
# Or check database connection in application.properties

# Rebuild backend
cd e:\CopFriendlyApp\backend
mvn clean install
```

### Frontend not starting?
```bash
# Reinstall dependencies
cd e:\CopFriendlyApp\frontend
npm install

# Clear cache and restart
npm run dev
```

### Can't login after approval?
```bash
# Clear browser cache
# Or open in incognito/private window
# Or clear localStorage:
localStorage.clear()
```

### Admin dashboard not showing users?
```bash
# Check browser console for errors (F12)
# Verify backend is running on port 8080
# Check network tab to see API calls
```

## 📋 API Testing (Optional)

### Test with curl or Postman

**Register User:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "Password123",
    "email": "new@example.com",
    "fullName": "New User",
    "firstName": "New",
    "lastName": "User",
    "phone": "1234567890",
    "dateOfBirth": "1990-01-01",
    "role": "traffic_sergeant",
    "confirmPassword": "Password123"
  }'
```

**Login (will fail for pending users):**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "Password123"
  }'
```

**List Pending Users (as admin):**
```bash
curl -X GET http://localhost:8080/api/auth/users/pending \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

**Approve User (as admin):**
```bash
curl -X POST http://localhost:8080/api/auth/users/{userId}/approve \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

## ✅ Verification Checklist

- [ ] Backend running on http://localhost:8080
- [ ] Frontend running on http://localhost:5173
- [ ] Can register new user
- [ ] New user cannot login (pending status)
- [ ] Admin can see pending users
- [ ] Admin can approve users
- [ ] Approved user can login
- [ ] Admin can reject users
- [ ] Rejected user cannot login
- [ ] Admin can suspend users
- [ ] Suspended user cannot login
- [ ] Admin can reactivate users
- [ ] Admin can change user roles
- [ ] Admin can delete users
- [ ] Activity log shows admin actions

## 🎉 Success!

If all checklist items pass, your user approval system is working perfectly!

---

**Need Help?**
- Check `USER_APPROVAL_SYSTEM_COMPLETE.md` for detailed documentation
- Check browser console (F12) for frontend errors
- Check backend terminal for Spring Boot errors
- Verify MongoDB/Database is running and connected

