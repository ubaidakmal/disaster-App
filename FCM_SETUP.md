# Firebase Cloud Messaging (FCM) Setup Guide

## Overview

This app uses Firebase Cloud Messaging (FCM) to send push notifications to all users when a new disaster report is created.

## Architecture

1. **Android App**: Creates disaster report and saves to Firestore
2. **Cloud Function**: Automatically detects new report in Firestore
3. **FCM Service**: Sends push notification to all users
4. **Android App**: Receives and displays notification

## Android App Setup (Already Done)

✅ FCM Service created: `app/src/main/java/com/bc230420212/app/service/FCMService.kt`
✅ Notification channel configured
✅ Auto-subscribe to "allUsers" topic in MainActivity
✅ Manifest configured with FCM service

## Cloud Function Setup (Required)

### Step 1: Install Firebase CLI

```bash
npm install -g firebase-tools
```

### Step 2: Login to Firebase

```bash
firebase login
```

### Step 3: Initialize Functions

```bash
cd /path/to/your/project
firebase init functions
```

When prompted:
- Select your Firebase project
- Choose JavaScript or TypeScript (JavaScript is in the code)
- Install dependencies: Yes

### Step 4: Deploy Functions

```bash
firebase deploy --only functions
```

### Step 5: Verify Deployment

1. Go to Firebase Console → Functions
2. You should see `sendReportNotification` function
3. Check logs to verify it's working

## How It Works

### When a Report is Created:

1. User creates disaster report in Android app
2. Report is saved to Firestore `reports` collection
3. Cloud Function `sendReportNotification` is automatically triggered
4. Function sends FCM notification to "allUsers" topic
5. All users receive notification on their phones

### Notification Content:

- **Title**: "New Disaster Report"
- **Body**: "{DisasterType} reported near your location"
  - Example: "Fire reported near your location"
  - Example: "Flood reported near your location"

### Notification Data:

- `reportId`: ID of the report
- `disasterType`: Type of disaster
- `latitude`: Report latitude
- `longitude`: Report longitude
- `timestamp`: When report was created

## Testing

### Test Notification from Firebase Console:

1. Go to Firebase Console → Cloud Messaging
2. Click "Send test message"
3. Enter FCM token (get from app logs)
4. Send notification

### Test with Real Report:

1. Create a disaster report in the app
2. Check Firebase Console → Functions → Logs
3. Verify notification was sent
4. Check other devices for notification

## Important Points for HOD

### Client-Server Architecture:

- **Client (Android App)**: 
  - Creates the report
  - Saves to Firestore
  - Receives notifications
  - Displays notifications

- **Server (Cloud Function)**:
  - Detects new reports automatically
  - Sends notifications to all users
  - Runs on Firebase servers (not in app)

### Why Cloud Functions?

- **Automatic**: No need to manually trigger notifications
- **Scalable**: Handles thousands of users
- **Reliable**: Runs on Firebase infrastructure
- **Cost-effective**: Free tier covers most use cases

## Troubleshooting

### Notifications Not Received:

1. Check Firebase Console → Functions → Logs for errors
2. Verify function is deployed: `firebase functions:list`
3. Check app is subscribed to topic (see MainActivity logs)
4. Verify FCM token is valid

### Function Not Triggering:

1. Check Firestore rules allow writes
2. Verify function is deployed correctly
3. Check Firebase Console → Functions → Logs

## Files

- **Android Service**: `app/src/main/java/com/bc230420212/app/service/FCMService.kt`
- **Cloud Function**: `functions/index.js`
- **Package Config**: `functions/package.json`

## Next Steps

1. Deploy Cloud Function to Firebase
2. Test with a real disaster report
3. Verify notifications are received
4. Monitor Firebase Console for any issues

