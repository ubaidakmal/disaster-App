# Push Notification Setup Guide

## Overview

This document explains how push notifications work in the Disaster Alert App. It's designed to help explain the architecture to your HOD (Head of Department).

## Architecture: Client-Server Model

### 🔵 Client Side (Android App)
**What it does:**
- User creates a disaster report
- App saves the report to Firebase Firestore database
- App receives and displays push notifications

**What it does NOT do:**
- ❌ Does NOT send notifications to other users
- ❌ Does NOT handle notification distribution

### 🟢 Server Side (Firebase Cloud Functions)
**What it does:**
- Automatically detects when a new report is created in Firestore
- Sends push notifications to ALL users subscribed to the topic
- Runs on Firebase servers (not in the Android app)

**Why Server-Side?**
- ✅ Automatic: No manual intervention needed
- ✅ Scalable: Can send to thousands of users instantly
- ✅ Reliable: Runs on Firebase infrastructure
- ✅ Secure: Server-side logic is protected

## How It Works: Step by Step

### Step 1: User Creates Report
```
User opens app → Fills report form → Clicks "Submit Report"
```

### Step 2: App Saves to Database
```
Android App → Firebase Firestore → Report saved to "reports" collection
```

### Step 3: Cloud Function Detects New Report
```
Firestore triggers Cloud Function → Function detects new document
```

### Step 4: Server Sends Notifications
```
Cloud Function → Firebase Cloud Messaging (FCM) → All users receive notification
```

### Step 5: Users Receive Notification
```
FCM → User's phone → Notification appears → User can tap to open app
```

## Notification Format

### Title:
```
"New Disaster Report"
```

### Body:
```
"{DisasterType} reported near your location"
```

**Examples:**
- "Fire reported near your location"
- "Flood reported near your location"
- "Earthquake reported near your location"
- "Accident reported near your location"

## Technical Implementation

### Android App Files:
1. **FCMService.kt** - Handles receiving notifications
2. **MainActivity.kt** - Subscribes users to "allUsers" topic
3. **AndroidManifest.xml** - Configures FCM service

### Server-Side Files:
1. **functions/index.js** - Cloud Function that sends notifications
2. **functions/package.json** - Node.js dependencies

## Deployment Instructions

### Prerequisites:
1. Firebase project created
2. Firebase CLI installed: `npm install -g firebase-tools`

### Deploy Cloud Function:

```bash
# 1. Login to Firebase
firebase login

# 2. Navigate to project directory
cd /path/to/AndroidbasedCrowdsourcedDisasterAlertSafetyApp

# 3. Initialize functions (if not done)
firebase init functions

# 4. Deploy the function
firebase deploy --only functions
```

### Verify Deployment:

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Select your project
3. Navigate to **Functions** section
4. You should see `sendReportNotification` function
5. Check **Logs** to see when it's triggered

## Testing

### Test 1: Create a Report
1. Open the app
2. Create a disaster report (e.g., Fire)
3. Submit the report
4. Check Firebase Console → Functions → Logs
5. You should see: "Successfully sent notification"

### Test 2: Receive Notification
1. Install app on multiple devices (or use emulator + real device)
2. Create a report from Device A
3. Device B should receive notification within seconds
4. Notification should show: "New Disaster Report" / "Fire reported near your location"

## Important Points for HOD Presentation

### 1. Separation of Concerns
- **App creates data** (report)
- **Server sends notifications** (push to users)
- Clear separation = better architecture

### 2. Scalability
- Can handle 1 user or 1 million users
- Firebase automatically scales
- No code changes needed

### 3. Reliability
- Server-side execution is more reliable
- Works even if app is closed
- Firebase handles retries automatically

### 4. Security
- Notification logic is server-side
- Cannot be tampered with by users
- Follows Firebase security best practices

### 5. Cost-Effective
- Firebase free tier: 2 million function invocations/month
- Free tier: 10,000 FCM messages/day
- Sufficient for most academic projects

## Flow Diagram

```
┌─────────────┐
│   User A    │
│  (Android)  │
└──────┬──────┘
       │
       │ 1. Creates Report
       │
       ▼
┌─────────────┐
│  Firestore  │
│  Database   │
└──────┬──────┘
       │
       │ 2. New Document Created
       │
       ▼
┌─────────────┐
│   Cloud     │
│  Function   │
└──────┬──────┘
       │
       │ 3. Detects New Report
       │    Generates Notification
       │
       ▼
┌─────────────┐
│     FCM     │
│  (Firebase  │
│  Messaging) │
└──────┬──────┘
       │
       │ 4. Sends to All Users
       │
       ▼
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   User B    │     │   User C    │     │   User D    │
│  (Android)  │     │  (Android)  │     │  (Android)  │
└─────────────┘     └─────────────┘     └─────────────┘
```

## Troubleshooting

### Notifications Not Sending:
1. Check Firebase Console → Functions → Logs
2. Verify function is deployed: `firebase functions:list`
3. Check Firestore rules allow writes
4. Verify function has proper permissions

### Notifications Not Received:
1. Check app is subscribed to "allUsers" topic (see MainActivity)
2. Verify FCM token is valid
3. Check device has internet connection
4. Verify notification permissions are granted

### Function Errors:
1. Check Firebase Console → Functions → Logs
2. Verify Node.js version (should be 18)
3. Check function dependencies are installed
4. Verify Firestore structure matches expected format

## Code Locations

### Android App:
- **FCM Service**: `app/src/main/java/com/bc230420212/app/service/FCMService.kt`
- **Main Activity**: `app/src/main/java/com/bc230420212/app/MainActivity.kt`
- **Manifest**: `app/src/main/AndroidManifest.xml`

### Server-Side:
- **Cloud Function**: `functions/index.js`
- **Package Config**: `functions/package.json`
- **Firebase Config**: `firebase.json`

## Summary

✅ **App creates report** → Saves to Firestore  
✅ **Server detects report** → Cloud Function triggers automatically  
✅ **Server sends notification** → FCM sends to all users  
✅ **Users receive notification** → On their phones  

**Key Point**: The Android app only creates the report. The server-side Cloud Function handles all notification logic automatically.


