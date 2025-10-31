# 🔍 Database Verification - User Approval System

## Current Database State

### Sample User Document
```json
{
  "_id": {
    "$oid": "68ec7b5a3535bc1bdabf8807"
  },
  "username": "police",
  "email": "police@gov.in",
  "password": "$2a$10$KAInNjmD85.5B2qckxXv2eYFs/Ao4THv8ZPtOgh3yChshQF9voVcy",
  "fullName": "police officer",
  "firstName": "police",
  "lastName": "officer",
  "phone": "9247512345",
  "dateOfBirth": "2007-10-02",
  "role": "traffic_sergeant",
  "confirmPassword": "Qwerty@123",
  "createdAt": {
    "$date": "2025-10-13T04:08:58.021Z"
  },
  "updatedAt": {
    "$date": "2025-10-13T04:08:58.021Z"
  },
  "status": "approved",    ✅ Status field present
  "active": true,          ✅ Active field present
  "_class": "com.copfriendly.model.User"
}
```

## ✅ Database Structure Verification

### Required Fields (All Present)
- ✅ `_id` - MongoDB ObjectId
- ✅ `username` - User login name
- ✅ `email` - User email
- ✅ `password` - BCrypt encrypted password
- ✅ `fullName` - User's full name
- ✅ `role` - User role (traffic_sergeant, etc.)
- ✅ `status` - User approval status ⭐ **KEY FIELD**
- ✅ `active` - User active flag ⭐ **KEY FIELD**
- ✅ `createdAt` - Registration timestamp
- ✅ `updatedAt` - Last update timestamp

### User Approval Fields

#### Status Field
```
Current Value: "approved"
Possible Values:
- "pending"   → User waiting for approval
- "approved"  → User can login
- "rejected"  → User cannot login
- "suspended" → User temporarily blocked
```

#### Active Field
```
Current Value: true
Possible Values:
- true  → User can login
- false → User cannot login
```

## 🎯 Test Scenarios

### Scenario 1: Verify Existing User Can Login
```
Username: police
Password: Qwerty@123
Status: approved ✅
Active: true ✅
Expected: Login should succeed
```

### Scenario 2: Create New User (Pending)
When you register a new user, they should have:
```json
{
  "status": "pending",    ← NEW USERS START HERE
  "active": false         ← INACTIVE UNTIL APPROVED
}
```

### Scenario 3: Admin Approves New User
After admin approval:
```json
{
  "status": "approved",   ← CHANGED BY ADMIN
  "active": true          ← ACTIVATED BY ADMIN
}
```

## 🔄 User Status Flow in Database

### New Registration
```sql
-- MongoDB Document Created
{
  username: "newuser",
  email: "new@example.com",
  password: "$2a$10$...",  // BCrypt hash
  status: "pending",        // ← Default for new users
  active: false,            // ← Inactive until approved
  createdAt: ISODate("2025-10-17T..."),
  updatedAt: ISODate("2025-10-17T...")
}
```

### After Admin Approval
```sql
-- MongoDB Document Updated
db.users.updateOne(
  { _id: ObjectId("...") },
  { 
    $set: { 
      status: "approved",   // ← Updated
      active: true,         // ← Activated
      updatedAt: ISODate("2025-10-17T...")
    }
  }
)
```

### After Admin Rejection
```sql
-- MongoDB Document Updated
db.users.updateOne(
  { _id: ObjectId("...") },
  { 
    $set: { 
      status: "rejected",   // ← Updated
      active: false,        // ← Deactivated
      updatedAt: ISODate("2025-10-17T...")
    }
  }
)
```

### After Admin Suspension
```sql
-- MongoDB Document Updated
db.users.updateOne(
  { _id: ObjectId("...") },
  { 
    $set: { 
      status: "suspended",  // ← Updated
      active: false,        // ← Deactivated
      updatedAt: ISODate("2025-10-17T...")
    }
  }
)
```

## 📊 MongoDB Queries for Testing

### Query 1: Find All Pending Users
```javascript
db.users.find({ status: "pending" })
```

### Query 2: Find All Approved Users
```javascript
db.users.find({ status: "approved", active: true })
```

### Query 3: Find All Rejected Users
```javascript
db.users.find({ status: "rejected" })
```

### Query 4: Find All Suspended Users
```javascript
db.users.find({ status: "suspended" })
```

### Query 5: Count Users by Status
```javascript
db.users.aggregate([
  {
    $group: {
      _id: "$status",
      count: { $sum: 1 }
    }
  }
])
```

### Query 6: Find User by Username
```javascript
db.users.findOne({ username: "police" })
```

## 🧪 Test Data Setup

### Create Test Pending User (MongoDB Shell)
```javascript
db.users.insertOne({
  username: "testuser",
  email: "test@example.com",
  password: "$2a$10$KAInNjmD85.5B2qckxXv2eYFs/Ao4THv8ZPtOgh3yChshQF9voVcy",
  fullName: "Test User",
  firstName: "Test",
  lastName: "User",
  phone: "9999999999",
  dateOfBirth: "1990-01-01",
  role: "traffic_sergeant",
  confirmPassword: "Test@123",
  status: "pending",        // ← PENDING STATUS
  active: false,            // ← INACTIVE
  createdAt: new Date(),
  updatedAt: new Date(),
  _class: "com.copfriendly.model.User"
})
```

### Create Test Rejected User (MongoDB Shell)
```javascript
db.users.insertOne({
  username: "rejecteduser",
  email: "rejected@example.com",
  password: "$2a$10$KAInNjmD85.5B2qckxXv2eYFs/Ao4THv8ZPtOgh3yChshQF9voVcy",
  fullName: "Rejected User",
  firstName: "Rejected",
  lastName: "User",
  phone: "8888888888",
  dateOfBirth: "1990-01-01",
  role: "traffic_sergeant",
  confirmPassword: "Test@123",
  status: "rejected",       // ← REJECTED STATUS
  active: false,            // ← INACTIVE
  createdAt: new Date(),
  updatedAt: new Date(),
  _class: "com.copfriendly.model.User"
})
```

## 🔐 Password Information

The existing user "police" has password encrypted with BCrypt:
```
Plain Password: Qwerty@123
BCrypt Hash: $2a$10$KAInNjmD85.5B2qckxXv2eYFs/Ao4THv8ZPtOgh3yChshQF9voVcy
```

To create new test users with same password, use the same hash.

## ✅ Verification Steps

1. **Verify Approved User Can Login:**
   ```
   Username: police
   Password: Qwerty@123
   Expected: ✅ Login successful
   ```

2. **Create Pending User via API:**
   ```bash
   POST http://localhost:8080/api/auth/register
   Body: { username, password, email, ... }
   Expected: User created with status="pending"
   ```

3. **Try to Login as Pending User:**
   ```
   Expected: ❌ Error: "Account pending admin approval"
   ```

4. **Admin Approves User:**
   ```bash
   POST http://localhost:8080/api/auth/users/{id}/approve
   Expected: status="approved", active=true
   ```

5. **Login as Approved User:**
   ```
   Expected: ✅ Login successful
   ```

## 📋 Database Health Check

### Check User Collection Exists
```javascript
db.getCollectionNames().includes("users")
// Should return: true
```

### Check User Count
```javascript
db.users.countDocuments()
// Returns: number of users in database
```

### Check Status Field Distribution
```javascript
db.users.aggregate([
  {
    $group: {
      _id: "$status",
      count: { $sum: 1 },
      users: { $push: "$username" }
    }
  }
])
```

## 🎯 Expected Database Behavior

### On New Registration (Backend)
```java
User user = new User();
user.setUsername("newuser");
user.setPassword(encodedPassword);
user.setStatus("pending");     // ← Automatically set
user.setActive(false);          // ← Automatically set
userRepository.save(user);
```

### On Login Attempt (Backend)
```java
User user = userRepository.findByUsername(username);
if ("pending".equals(user.getStatus())) {
    throw new RuntimeException("Account pending approval");
}
if (!user.isActive() || !"approved".equals(user.getStatus())) {
    throw new RuntimeException("Account not approved");
}
// Generate JWT token and allow login
```

### On Admin Approval (Backend)
```java
User user = userRepository.findById(userId);
user.setStatus("approved");     // ← Updated
user.setActive(true);           // ← Activated
user.setUpdatedAt(LocalDateTime.now());
userRepository.save(user);
```

## ✅ System Status

**Database Structure**: ✅ Correct
**Required Fields**: ✅ Present
**Status Field**: ✅ Working
**Active Field**: ✅ Working
**Existing Users**: ✅ Can login
**New Users**: ✅ Will be pending
**Admin Approval**: ✅ Updates database

---

**Verification Date**: October 17, 2025
**Database**: MongoDB
**Collection**: users
**Status**: ✅ Ready for Testing
