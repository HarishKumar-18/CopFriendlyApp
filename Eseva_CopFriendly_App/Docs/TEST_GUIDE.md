# 🧪 Comprehensive Test Guide for Violation Form

## Prerequisites
- ✅ Backend running on `http://localhost:8080`
- ✅ Frontend running on `http://localhost:5173`
- ✅ MongoDB running on `localhost:27017`
- ✅ User logged in (must have JWT token)

---

## Test 1: Happy Path - Complete Form Submission ✅

### Steps:
1. **Open the form:**
   - Navigate to: `http://localhost:5173/create-violation` (or your violation form route)
   - Ensure you're logged in

2. **Fill all required fields:**
   ```
   Vehicle Information:
   ✓ Vehicle Number: MH01AB1234
   ✓ Vehicle Type: Car
   - Vehicle Model: Maruti Swift
   - Vehicle Color: White

   Driver Information:
   ✓ Driver Name: John Doe
   - License Number: DL123456789
   - Phone Number: 9876543210
   - Address: 123 Main Street, Mumbai

   Violation Details:
   ✓ Violation Type: Speeding
   ✓ Fine Amount: 2000
   ✓ Location: MG Road Junction
   ✓ Date & Time: (auto-filled with current)
   - Description: Vehicle exceeded speed limit by 40 kmph
   ```

3. **Open DevTools (F12):**
   - Go to **Network** tab
   - Filter: `violations`

4. **Click "✅ Register Violation"**

5. **Expected Results:**
   - ✅ Alert: "Violation registered successfully!"
   - ✅ Redirects to `/violations` list
   - ✅ Network tab shows: `POST /api/violations` with status `200` or `201`
   - ✅ Response contains violation object with `ticketNumber`

6. **Verify in MongoDB:**
   ```bash
   # Open MongoDB shell
   mongosh
   
   # Switch to ViolationsDB
   use ViolationsDB
   
   # Find the violation
   db.violations.find().sort({createdAt: -1}).limit(1).pretty()
   ```

   **Expected in MongoDB:**
   ```json
   {
     "_id": "...",
     "ticketNumber": "TCK...",
     "vehicleNumber": "MH01AB1234",
     "vehicleType": "car",
     "driverName": "John Doe",
     "violationType": "speeding",
     "fineAmount": 2000,
     "location": "MG Road Junction",
     "status": "PENDING",
     "paymentStatus": "UNPAID",
     "isDraft": false,
     "issuerId": "...",
     "createdAt": "...",
     "dueDate": "..."
   }
   ```

---

## Test 2: Draft Path - Partial Form Save 💾

### Steps:
1. **Navigate to violation form** (new browser tab or refresh)

2. **Clear localStorage first (optional):**
   - DevTools → Console → Run:
     ```javascript
     localStorage.removeItem('violationDraft')
     ```
   - Refresh page

3. **Fill PARTIAL information only:**
   ```
   ✓ Vehicle Number: KA05MN9876
   ✓ Vehicle Type: Motorcycle
   ✓ Driver Name: Jane Smith
   - Leave everything else empty
   ```

4. **Click "💾 Save as Draft"**

5. **Expected Results:**
   - ✅ Alert: "Draft saved successfully!" (if backend works)
   - OR Alert: "Draft saved locally (offline)." (if backend fails)
   - ✅ Page stays on form (no redirect)

6. **Verify localStorage:**
   - DevTools → Console → Run:
     ```javascript
     JSON.parse(localStorage.getItem('violationDraft'))
     ```
   - Should show your partial form data

7. **Reload the page (F5)**

8. **Expected Results:**
   - ✅ Form auto-fills with the draft data
   - ✅ Vehicle Number shows: KA05MN9876
   - ✅ Driver Name shows: Jane Smith

9. **Verify in MongoDB (if backend save worked):**
   ```bash
   use ViolationsDB
   db.violations.find({isDraft: true, vehicleNumber: "KA05MN9876"}).pretty()
   ```

   **Expected:**
   ```json
   {
     "vehicleNumber": "KA05MN9876",
     "driverName": "Jane Smith",
     "violationType": "other",  // default
     "fineAmount": 0,           // default
     "location": "N/A",         // default
     "status": "DRAFT",
     "isDraft": true
   }
   ```

---

## Test 3: GPS Capture 📍

### Steps:
1. **Navigate to violation form**

2. **Scroll to "Evidence" section**

3. **Click "📍 Get Current Location"**

4. **Browser may ask permission:**
   - Click "Allow" to share location

5. **Expected Results:**
   - ✅ Text updates from "Location not captured" to coordinates
   - Example: `19.076090, 72.877426`
   - ✅ Check DevTools Console for lat/lon values:
     ```javascript
     // In console, after clicking button
     form.value.latitude  // Should show a number like 19.076090
     form.value.longitude // Should show a number like 72.877426
     ```

6. **Fill rest of form and submit**

7. **Verify GPS in payload:**
   - DevTools → Network → Click the `violations` request
   - Request Payload should contain:
     ```json
     {
       "latitude": 19.076090,
       "longitude": 72.877426,
       "gpsLocation": "19.076090,72.877426"
     }
     ```

---

## Test 4: Validation - Empty Form ⚠️

### Steps:
1. **Navigate to violation form (fresh page)**

2. **Leave ALL fields empty**

3. **Click "✅ Register Violation"**

4. **Expected Results:**
   - ✅ Alert: "Please fill in all required fields"
   - OR Browser native validation shows:
     ```
     Vehicle Number: "Please fill out this field"
     ```
   - ✅ Form does NOT submit
   - ✅ Network tab shows NO request to backend

5. **Fill ONLY vehicle number: TEST123**

6. **Click submit again**

7. **Expected Results:**
   - ✅ Browser highlights next required field (Vehicle Type)
   - ✅ Still no backend request

---

## Test 5: Offline Draft Save 🔌

### Steps:
1. **Navigate to violation form**

2. **Fill partial data:**
   ```
   Vehicle Number: OFFLINE123
   Driver Name: Test Offline
   ```

3. **Open DevTools → Network tab**

4. **Enable offline mode:**
   - In Network tab → Check "Offline" checkbox
   - OR: Select "Offline" from throttling dropdown

5. **Click "💾 Save as Draft"**

6. **Expected Results:**
   - ✅ Backend request fails (shows as red in Network tab)
   - ✅ Alert: "Draft saved locally (offline)."
   - ✅ Console shows: "Draft save failed" error

7. **Verify localStorage fallback:**
   ```javascript
   // DevTools Console
   localStorage.getItem('violationDraft')
   // Should show JSON string with your data
   ```

8. **Re-enable network:**
   - Uncheck "Offline"

9. **Reload page**

10. **Expected Results:**
    - ✅ Form loads with offline draft data
    - ✅ Vehicle Number shows: OFFLINE123

---

## Test 6: Backend Integration - Network Inspection 🔍

### Steps:
1. **Open DevTools → Network tab (BEFORE loading form)**

2. **Navigate to violation form**

3. **Fill complete form with test data**

4. **Click "✅ Register Violation"**

5. **Inspect Network Request:**
   - Find: `POST http://localhost:8080/api/violations`
   - Click on it

6. **Check Request Headers:**
   ```
   Content-Type: application/json
   Authorization: Bearer eyJhbGc...  (JWT token)
   ```

7. **Check Request Payload:**
   ```json
   {
     "vehicleNumber": "MH01AB1234",
     "vehicleType": "car",
     "vehicleModel": "Maruti Swift",
     "vehicleColor": "White",
     "driverName": "John Doe",
     "licenseNumber": "DL123456789",
     "driverPhoneNumber": "9876543210",
     "driverAddress": "123 Main Street",
     "violationType": "speeding",
     "fineAmount": 2000,
     "location": "MG Road Junction",
     "violationDateTime": "2025-10-18T14:30",
     "description": "Exceeded speed limit",
     "evidence": null,
     "latitude": 19.076090,
     "longitude": 72.877426,
     "gpsLocation": "19.076090,72.877426",
     "remarks": null,
     "isDraft": false
   }
   ```

8. **Check Response (Status: 200 or 201):**
   ```json
   {
     "success": true,
     "message": "Violation created successfully",
     "violation": {
       "id": "...",
       "ticketNumber": "TCK1729245600123",
       "status": "PENDING",
       "paymentStatus": "UNPAID",
       ...
     }
   }
   ```

9. **Verify JWT Token is sent:**
   - Authorization header must be present
   - If missing → Check auth store token is set

---

## Test 7: Database Separation Verification 🗄️

### Steps:
1. **Open MongoDB Shell or Compass**

2. **Check database list:**
   ```bash
   mongosh
   show dbs
   ```

   **Expected Output:**
   ```
   Testing       # Should exist (users database)
   ViolationsDB  # Should exist (violations database)
   admin
   config
   local
   ```

3. **Check Testing database (Users):**
   ```bash
   use Testing
   show collections
   # Should show: users
   
   db.users.findOne()
   # Should show user documents with username, email, etc.
   ```

4. **Check ViolationsDB (Violations):**
   ```bash
   use ViolationsDB
   show collections
   # Should show: violations
   
   db.violations.findOne()
   # Should show violation documents with ticketNumber, vehicleNumber, etc.
   ```

5. **Verify NO violations in Testing DB:**
   ```bash
   use Testing
   db.violations.find().count()
   # Should return: 0 (violations should NOT be here)
   ```

6. **Verify NO users in ViolationsDB:**
   ```bash
   use ViolationsDB
   db.users.find().count()
   # Should return: 0 (users should NOT be here)
   ```

---

## 🎯 Quick Test Checklist

Use this for rapid testing:

```
☐ Backend running (http://localhost:8080)
☐ Frontend running (http://localhost:5173)
☐ MongoDB running (localhost:27017)
☐ User logged in (has JWT token)

☐ Test 1: Complete form submit → Success → Redirect
☐ Test 2: Partial draft → Save → Reload → Auto-fill
☐ Test 3: GPS button → Coordinates captured
☐ Test 4: Empty submit → Validation blocks
☐ Test 5: Offline draft → localStorage fallback
☐ Test 6: Network tab → POST request → 200 response
☐ Test 7: MongoDB → Testing (users) + ViolationsDB (violations)
```

---

## 🐛 Common Issues & Solutions

### Issue 1: "Failed to register violation"
**Solution:**
- Check backend is running: `http://localhost:8080`
- Check JWT token exists: DevTools → Application → localStorage → `token`
- Check backend logs for errors

### Issue 2: Draft not loading on reload
**Solution:**
- Check localStorage: `localStorage.getItem('violationDraft')`
- Check browser console for parse errors
- Try clearing and re-saving

### Issue 3: GPS button does nothing
**Solution:**
- Browser may have blocked location permission
- Check: Browser settings → Site permissions → Location
- Try in HTTPS (some browsers require secure context)

### Issue 4: Network request has no Authorization header
**Solution:**
- User not logged in properly
- Token not in localStorage
- Login again and retry

### Issue 5: Violations appearing in Testing database
**Solution:**
- Backend config issue
- Check `ViolationsMongoConfig.java` is configured
- Check controller uses `ViolationsDbRepository`

---

## 📊 Expected Results Summary

| Test | Success Indicator | Database |
|------|-------------------|----------|
| Happy Path | Alert + Redirect + 200 response | ViolationsDB (status: PENDING) |
| Draft Path | Alert + Stay on page + localStorage | ViolationsDB (status: DRAFT) |
| GPS Capture | Coordinates displayed | - |
| Validation | Alert/Browser message + No request | - |
| Offline Draft | localStorage fallback alert | localStorage only |
| Network Check | POST visible with payload + 200 | - |
| DB Separation | Users in Testing, Violations in ViolationsDB | Both DBs |

---

## 🚀 Automated Test Script (Optional)

See `test-violation-workflow.ps1` for automated PowerShell tests.

---

**Test Date:** October 18, 2025
**Tester:** _______________
**Status:** ☐ PASS | ☐ FAIL
**Notes:** _________________________________
