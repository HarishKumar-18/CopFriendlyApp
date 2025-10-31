# 🚀 Quick Test - Start NOW!

## ✅ System Running
- Backend: http://localhost:8080 ✅
- Frontend: http://localhost:5173 ✅

---

## 🎯 Test Flow (5 Minutes)

### Step 1: Login as Existing User (30 seconds)
1. Open: http://localhost:5173/login
2. Login:
   - Username: `police`
   - Password: `Qwerty@123`
   - Role: `Traffic Sergeant`
3. ✅ Should redirect to Traffic Sergeant Dashboard

---

### Step 2: Register New Test User (1 minute)
1. Logout or open incognito window
2. Go to: http://localhost:5173/register
3. Fill form:
   ```
   Username: testuser123
   Email: testuser@test.com
   Password: Test@1234
   Confirm: Test@1234
   Full Name: Test User
   First Name: Test
   Last Name: User
   Phone: 9876543210
   DOB: 1990-01-01
   Role: Traffic Sergeant
   ```
4. Click Register
5. ✅ Should show success message

---

### Step 3: Try Login as Pending User (30 seconds)
1. Try to login with:
   - Username: `testuser123`
   - Password: `Test@1234`
2. ❌ Should FAIL with error:
   > "Your account is pending admin approval. Please wait for approval before logging in."

---

### Step 4: Login as Admin (30 seconds)
1. Logout/new window
2. Login as:
   - Username: `admin`
   - Password: `admin123`
   - Role: `System Administrator`
3. ✅ Should see Admin Dashboard

---

### Step 5: Approve the New User (1 minute)
1. On Admin Dashboard, you should see:
   - Statistics: Pending Approvals = 1 (or more)
   - User "testuser123" in pending list
2. Click "✅ Approve" button next to testuser123
3. Confirm approval
4. ✅ Success message should appear
5. ✅ User disappears from pending list

---

### Step 6: Login as Approved User (30 seconds)
1. Logout from admin
2. Login with approved user:
   - Username: `testuser123`
   - Password: `Test@1234`
   - Role: `Traffic Sergeant`
3. ✅ Should LOGIN SUCCESSFULLY
4. ✅ Should redirect to Traffic Sergeant Dashboard

---

## 🎉 SUCCESS!

If all 6 steps worked, your user approval system is **100% functional**! 

---

## 🔍 Bonus: Check Database

Open MongoDB and run:
```javascript
// See all users
db.users.find().pretty()

// See the test user you just created
db.users.findOne({ username: "testuser123" })

// Should show:
// status: "approved"
// active: true
```

---

## 🧪 More Tests?

For comprehensive testing (12 tests total), see: `COMPLETE_TEST_SCRIPT.md`

Tests include:
- Reject users
- Suspend users
- Reactivate users
- Delete users
- Change roles
- Search/Filter

---

**Ready? Start with Step 1!** 🎯
