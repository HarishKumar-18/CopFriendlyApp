# 🔍 Check What Users Exist in Database

## Quick Test - Open in Browser

**Check all users:**
```
http://localhost:8080/api/users
```

**Check pending users:**
```
http://localhost:8080/api/users/pending
```

**Check approved users:**
```
http://localhost:8080/api/users/status/approved
```

---

## Current Situation

Based on the database document you shared earlier, you have **ONE user**:

```json
{
  "_id": {"$oid": "67117c95ceb6827f00b18851"},
  "username": "police",
  "password": "$2a$10$8scKU5aPLQKLq5LYPpJZG.jSmJILQX4iQlqGZtRx0Ej20Jq8lPcBu",
  "email": "police@gov.in",
  "fullName": "police officer",
  "firstName": "police",
  "lastName": "officer",
  "phone": "9876543210",
  "role": "traffic_sergeant",
  "status": "approved",
  "active": true,
  "dateOfBirth": "1993-10-18",
  "createdAt": {"$date": {"$numberLong": "1729171605815"}},
  "updatedAt": {"$date": {"$numberLong": "1729171605815"}},
  "_class": "com.copfriendly.model.User"
}
```

---

## ✅ To Login Successfully

**Use these credentials:**
- Username: `police`
- Password: `Qwerty@123`
- Role: `Traffic Sergeant`

---

## 🆕 To Create "medical" User

1. **Register** at: http://localhost:5173/register
   - Username: `medical`
   - Email: `medical@test.com`
   - Password: `Qwerty@123`
   - Confirm Password: `Qwerty@123`
   - Full Name: `Medical Officer`
   - First Name: `Medical`
   - Last Name: `Officer`
   - Phone: `9876543211`
   - Date of Birth: `1990-01-01`
   - Role: `Medical Emergency Central`

2. **User will be pending** - cannot login yet

3. **Login as admin** to approve:
   - Username: `admin` (if exists) or `police`
   - Go to admin dashboard
   - Approve the "medical" user

4. **Then login as medical** with the credentials you set

---

## 🔧 Alternative: Add User Directly to MongoDB

If you want to skip the approval process, you can add a user directly in MongoDB:

```javascript
db.users.insertOne({
  "username": "medical",
  "password": "$2a$10$8scKU5aPLQKLq5LYPpJZG.jSmJILQX4iQlqGZtRx0Ej20Jq8lPcBu",  // This is "Qwerty@123" hashed
  "email": "medical@test.com",
  "fullName": "Medical Officer",
  "firstName": "Medical",
  "lastName": "Officer",
  "phone": "9876543211",
  "role": "medical_emergency_central",
  "status": "approved",
  "active": true,
  "dateOfBirth": "1990-01-01",
  "createdAt": new Date(),
  "updatedAt": new Date(),
  "_class": "com.copfriendly.model.User"
})
```

Then you can login with:
- Username: `medical`
- Password: `Qwerty@123`

---

**Bottom Line:** The username "medical" doesn't exist in your database yet. Either login with "police" or create the "medical" user first! 🎯
