# 🎨 User Approval System - Visual Flow Diagrams

## 📊 System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         FRONTEND (Vue.js)                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │  LoginView   │  │  AdminDash   │  │   RegisterView       │  │
│  └──────┬───────┘  └──────┬───────┘  └──────────┬───────────┘  │
│         │                 │                      │              │
│         └─────────────────┼──────────────────────┘              │
│                           │                                     │
│                    ┌──────▼──────┐                              │
│                    │  auth.js    │  (Pinia Store)               │
│                    │  (Store)    │                              │
│                    └──────┬──────┘                              │
│                           │                                     │
│                    ┌──────▼──────┐                              │
│                    │   api.js    │  (API Service)               │
│                    └──────┬──────┘                              │
└───────────────────────────┼─────────────────────────────────────┘
                            │ HTTP Requests
                            │
┌───────────────────────────▼─────────────────────────────────────┐
│                      BACKEND (Spring Boot)                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │AuthController│  │UserController│  │  SecurityConfig      │  │
│  └──────┬───────┘  └──────┬───────┘  └──────────────────────┘  │
│         │                 │                                     │
│         └─────────────────┼─────────────────────────────────┐  │
│                           │                                 │  │
│                    ┌──────▼──────┐                ┌─────────▼─┐ │
│                    │ AuthService │                │  JwtUtil  │ │
│                    └──────┬──────┘                └───────────┘ │
│                           │                                     │
│                    ┌──────▼──────┐                              │
│                    │UserRepository│                             │
│                    └──────┬──────┘                              │
└───────────────────────────┼─────────────────────────────────────┘
                            │
                    ┌───────▼────────┐
                    │   MongoDB      │
                    │   Database     │
                    └────────────────┘
```

## 🔄 User Registration Flow

```
┌─────────────┐
│    User     │
│  (Browser)  │
└──────┬──────┘
       │
       │ 1. Fill Registration Form
       │    - username, email, password
       │    - role, personal details
       ▼
┌─────────────────┐
│  Frontend Form  │
│  (RegisterView) │
└────────┬────────┘
         │
         │ 2. POST /api/auth/register
         │    { username, password, email, role, ... }
         ▼
┌────────────────────┐
│  AuthController    │
│  register()        │
└────────┬───────────┘
         │
         │ 3. Create User
         ▼
┌────────────────────┐
│   AuthService      │
│   register()       │
│                    │
│   user.status =    │
│     "pending"      │
│   user.active =    │
│     false          │
└────────┬───────────┘
         │
         │ 4. Save to Database
         ▼
┌────────────────────┐
│   UserRepository   │
│   save(user)       │
└────────┬───────────┘
         │
         │ 5. User Saved
         │    (status: pending)
         ▼
┌────────────────────┐
│   Database         │
│   ✅ User stored   │
│   ⏳ Status: PENDING│
└────────┬───────────┘
         │
         │ 6. Return Response
         │    (NO JWT TOKEN)
         ▼
┌────────────────────┐
│   Frontend         │
│   "Account created"│
│   "Awaiting        │
│    approval"       │
└────────────────────┘
```

## 🚫 Login Attempt (Pending User)

```
┌─────────────┐
│    User     │
│  (Browser)  │
└──────┬──────┘
       │
       │ 1. Enter Credentials
       │    username: newuser
       │    password: ********
       ▼
┌─────────────────┐
│  Frontend Form  │
│  (LoginView)    │
└────────┬────────┘
         │
         │ 2. POST /api/auth/login
         │    { username, password }
         ▼
┌────────────────────┐
│  AuthController    │
│  login()           │
└────────┬───────────┘
         │
         │ 3. Authenticate
         ▼
┌────────────────────────────┐
│   AuthService              │
│   login()                  │
│                            │
│   ✅ Password Valid        │
│   ❌ Check Status          │
│                            │
│   if (status == "pending") │
│      throw Exception       │
└────────┬───────────────────┘
         │
         │ 4. Exception Thrown
         │    "Account pending
         │     admin approval"
         ▼
┌────────────────────┐
│  AuthController    │
│  catch (Exception) │
│  return 400        │
└────────┬───────────┘
         │
         │ 5. Error Response
         ▼
┌────────────────────┐
│   Frontend         │
│   Display Error:   │
│   "Your account is │
│   pending admin    │
│   approval"        │
│   ❌ Login Failed  │
└────────────────────┘
```

## ✅ Admin Approval Flow

```
┌─────────────┐
│   Admin     │
│  (Browser)  │
└──────┬──────┘
       │
       │ 1. Login as Admin
       │    username: admin
       │    password: admin123
       ▼
┌─────────────────────┐
│  Admin Dashboard    │
│  (AdminDashboardView)│
└────────┬────────────┘
         │
         │ 2. GET /api/users
         │    Fetch all users
         ▼
┌────────────────────┐
│  UserController    │
│  getAllUsers()     │
└────────┬───────────┘
         │
         │ 3. Return Users
         │    [{ id, status, ... }]
         ▼
┌────────────────────────┐
│  Admin Dashboard       │
│  Display Users:        │
│  ⏳ Pending: 5         │
│  ✅ Approved: 12       │
│  ❌ Rejected: 2        │
│  🚫 Suspended: 1       │
└────────┬───────────────┘
         │
         │ 4. Admin clicks "Approve"
         │    on pending user
         ▼
┌────────────────────────┐
│  Frontend              │
│  approveUser(userId)   │
└────────┬───────────────┘
         │
         │ 5. POST /api/auth/users/{id}/approve
         ▼
┌────────────────────┐
│  AuthController    │
│  approveUser(id)   │
└────────┬───────────┘
         │
         │ 6. Update User
         ▼
┌─────────────────────────┐
│   AuthService           │
│   approveUser(id)       │
│                         │
│   user.status =         │
│     "approved"          │
│   user.active = true    │
└────────┬────────────────┘
         │
         │ 7. Save to Database
         ▼
┌────────────────────┐
│   Database         │
│   ✅ Status: APPROVED│
│   ✅ Active: true    │
└────────┬───────────┘
         │
         │ 8. Success Response
         ▼
┌────────────────────────┐
│  Admin Dashboard       │
│  "User Approved!"      │
│  Refresh user list     │
│  Update statistics     │
└────────────────────────┘
```

## ✅ Login Attempt (Approved User)

```
┌─────────────┐
│    User     │
│  (Browser)  │
└──────┬──────┘
       │
       │ 1. Enter Credentials
       │    username: approved_user
       │    password: ********
       ▼
┌─────────────────┐
│  Frontend Form  │
│  (LoginView)    │
└────────┬────────┘
         │
         │ 2. POST /api/auth/login
         │    { username, password }
         ▼
┌────────────────────┐
│  AuthController    │
│  login()           │
└────────┬───────────┘
         │
         │ 3. Authenticate
         ▼
┌─────────────────────────────┐
│   AuthService               │
│   login()                   │
│                             │
│   ✅ Password Valid         │
│   ✅ Status: "approved"     │
│   ✅ Active: true           │
│                             │
│   Generate JWT Token        │
└────────┬────────────────────┘
         │
         │ 4. JWT Token Created
         │    eyJhbGciOiJIUzI1...
         ▼
┌────────────────────┐
│  AuthController    │
│  return {          │
│    token: "...",   │
│    user: {...}     │
│  }                 │
└────────┬───────────┘
         │
         │ 5. Success Response
         ▼
┌────────────────────┐
│   Frontend         │
│   Store token      │
│   Store user info  │
│   ✅ Login Success │
│   Redirect to      │
│   Dashboard        │
└────────────────────┘
```

## 📊 User Status State Machine

```
                    ┌─────────────┐
                    │   PENDING   │ ← New Registration
                    │   (Default) │
                    └──────┬──────┘
                           │
            ┌──────────────┼──────────────┐
            │              │              │
            │ Admin        │ Admin        │ Admin
            │ Approve      │ Reject       │ (Delete)
            ▼              ▼              ▼
    ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
    │   APPROVED   │ │   REJECTED   │ │   DELETED    │
    │ ✅ Can Login │ │ ❌ Cannot     │ │ (Removed)    │
    └──────┬───────┘ └──────────────┘ └──────────────┘
           │
           │ Admin
           │ Suspend
           ▼
    ┌──────────────┐
    │  SUSPENDED   │
    │ 🚫 Cannot    │
    │    Login     │
    └──────┬───────┘
           │
           │ Admin
           │ Reactivate
           ▼
    ┌──────────────┐
    │   APPROVED   │
    │ ✅ Can Login │
    └──────────────┘
```

## 🎯 Admin Dashboard Components

```
┌─────────────────────────────────────────────────────────────┐
│                    ADMIN DASHBOARD                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │          STATISTICS CARDS (Real-time)                │  │
│  │  ┌────────────┐┌────────────┐┌────────────┐┌───────┐│  │
│  │  │⏳ Pending  ││✅ Active   ││🚫 Suspended││👥 Total││  │
│  │  │    5       ││    12      ││    1       ││   20   ││  │
│  │  └────────────┘└────────────┘└────────────┘└───────┘│  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              QUICK FILTERS                           │  │
│  │  [Pending] [Approved] [Rejected] [Suspended] [All]   │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │           SEARCH & FILTER                            │  │
│  │  [Search: ____________] [Role: All ▼] [Clear]       │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                USERS TABLE                           │  │
│  ├────────────┬────────┬─────────┬───────────┬─────────┤  │
│  │ User Info  │ Role   │ Status  │ Signup    │ Actions │  │
│  ├────────────┼────────┼─────────┼───────────┼─────────┤  │
│  │ John Doe   │ Traffic│⏳ Pending│ 10/17/25  │[Approve]│  │
│  │ john@...   │Sergeant│         │           │[Reject] │  │
│  ├────────────┼────────┼─────────┼───────────┼─────────┤  │
│  │ Jane Smith │ Medical│✅ Approved│10/15/25  │[Suspend]│  │
│  │ jane@...   │ Team   │         │           │[View]   │  │
│  └────────────┴────────┴─────────┴───────────┴─────────┘  │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │             ACTIVITY LOG                             │  │
│  │  • User Approved: John (TS-101) - 2 mins ago        │  │
│  │  • User Suspended: Mike (TI-202) - 1 hour ago       │  │
│  │  • Role Changed: Sarah → Central Team - 3 hours ago │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 🔐 Security Layers

```
┌──────────────────────────────────────────────────────────┐
│                    SECURITY LAYERS                        │
├──────────────────────────────────────────────────────────┤
│                                                          │
│  Layer 1: Frontend (Vue.js)                             │
│  ┌────────────────────────────────────────────────┐    │
│  │ • Hide admin features from non-admin users     │    │
│  │ • Show appropriate error messages              │    │
│  │ • Client-side validation                       │    │
│  └────────────────────────────────────────────────┘    │
│                          │                              │
│  Layer 2: API Layer (Controllers)                      │
│  ┌────────────────────────────────────────────────┐    │
│  │ • Verify JWT token                             │    │
│  │ • Check admin role                             │    │
│  │ • Validate request data                        │    │
│  └────────────────────────────────────────────────┘    │
│                          │                              │
│  Layer 3: Service Layer (Business Logic)               │
│  ┌────────────────────────────────────────────────┐    │
│  │ • Check user status                            │    │
│  │ • Verify active flag                           │    │
│  │ • Apply business rules                         │    │
│  └────────────────────────────────────────────────┘    │
│                          │                              │
│  Layer 4: Database Layer                               │
│  ┌────────────────────────────────────────────────┐    │
│  │ • Store encrypted passwords (BCrypt)           │    │
│  │ • Store user status                            │    │
│  │ • Store active flag                            │    │
│  └────────────────────────────────────────────────┘    │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

## 📱 User Experience Flow

```
NEW USER REGISTRATION
        │
        ▼
┌───────────────┐
│ "Welcome!     │
│  Account      │
│  created      │
│  successfully"│
└───────┬───────┘
        │
        ▼
┌───────────────┐
│ Try to Login  │
└───────┬───────┘
        │
        ▼
┌────────────────────┐
│ ⚠️ Error:          │
│ "Your account is   │
│  pending admin     │
│  approval.         │
│  Please wait."     │
└────────┬───────────┘
        │
        ▼
┌───────────────┐
│ Wait for      │
│ Admin         │
│ Approval      │
└───────┬───────┘
        │
        │ (Admin approves)
        │
        ▼
┌───────────────┐
│ Try Login     │
│ Again         │
└───────┬───────┘
        │
        ▼
┌────────────────────┐
│ ✅ Login Success!  │
│ Welcome to your    │
│ dashboard          │
└────────────────────┘
```

---

**Created**: October 17, 2025
**Purpose**: Visual documentation of User Approval System
**Status**: ✅ Complete
