# 🔄 Clear Authentication Cache Guide

## Problem
You're seeing the dashboard directly because your browser has cached authentication data (login token and user info) from a previous session.

## Quick Solutions

### Option 1: Use Cache Clear Page (Easiest) ✨
Visit this URL in your browser:
```
http://localhost:5175/clear-cache.html
```

This page will:
- Show you what's cached
- Clear all authentication data with one click
- Redirect you to login page

### Option 2: Clear Manually in Browser

#### Chrome / Edge:
1. Press `F12` to open Developer Tools
2. Click **Application** tab
3. Expand **Local Storage** in left sidebar
4. Click on `http://localhost:5175`
5. Right-click on any item → **Clear**
6. Close DevTools
7. Press `Ctrl + Shift + R` to hard refresh

#### Firefox:
1. Press `F12` to open Developer Tools
2. Click **Storage** tab
3. Expand **Local Storage**
4. Click on `http://localhost:5175`
5. Right-click → **Delete All**
6. Press `Ctrl + Shift + R` to hard refresh

### Option 3: Use Incognito/Private Mode
Open a new incognito/private window:
- **Chrome**: `Ctrl + Shift + N`
- **Firefox**: `Ctrl + Shift + P`
- **Edge**: `Ctrl + Shift + N`

Then visit: `http://localhost:5175`

### Option 4: Clear All Browser Data
1. `Ctrl + Shift + Delete` (opens Clear Browsing Data)
2. Select:
   - ✅ Cached images and files
   - ✅ Cookies and other site data
3. Click **Clear data**
4. Refresh the page

## What Gets Cached?

The app stores these items in `localStorage`:
- `token` - JWT authentication token
- `user` - User profile information (name, role, etc.)

When you visit the app, it checks for these items and automatically logs you in if found.

## Verify Cache is Cleared

After clearing, you should:
1. See the login page
2. NOT be automatically logged in
3. Need to enter credentials to access dashboard

## Prevention

If you want to always start fresh during development:
1. Always use Incognito mode
2. Or manually logout before closing browser
3. Or clear cache after each session

## Current URLs

- **Main App**: http://localhost:5175
- **Cache Clear Page**: http://localhost:5175/clear-cache.html
- **Login Direct**: http://localhost:5175/login

## Test Credentials

After clearing cache, try logging in with:
- Username: `admin`
- Password: `admin123`

(Note: These are mock credentials - backend not connected yet)

---

✅ Your frontend is working correctly - the "problem" is just cached authentication!
