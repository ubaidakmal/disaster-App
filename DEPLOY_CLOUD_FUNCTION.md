# Deploy Cloud Function for Push Notifications

## ⚠️ IMPORTANT: Cloud Function Must Be Deployed

**The app creates reports, but notifications are sent by the Cloud Function running on Firebase servers.**

If you're not receiving notifications, the Cloud Function is likely not deployed yet.

## Quick Deployment Steps

### Step 1: Install Firebase CLI (if not installed)

```bash
npm install -g firebase-tools
```

### Step 2: Login to Firebase

```bash
firebase login
```

This will open a browser window. Login with your Google account that has access to the Firebase project.

### Step 3: Navigate to Project Directory

```bash
cd /Users/mac/Documents/projects/AndroidbasedCrowdsourcedDisasterAlertSafetyApp
```

### Step 4: Initialize Functions (First Time Only)

```bash
firebase init functions
```

When prompted:
- **Select your Firebase project** (the one connected to your app)
- **Language**: JavaScript (the code is already in JavaScript)
- **Install dependencies**: Yes

### Step 5: Deploy the Function

```bash
firebase deploy --only functions
```

This will deploy the `sendReportNotification` function to Firebase.

### Step 6: Verify Deployment

```bash
firebase functions:list
```

You should see:
```
sendReportNotification
```

## Verify It's Working

### 1. Check Function Logs

After deploying, create a test report in the app, then check logs:

```bash
firebase functions:log
```

You should see:
```
Successfully sent notification: [message-id]
```

### 2. Check Firebase Console

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Select your project
3. Go to **Functions** section
4. You should see `sendReportNotification` function
5. Click on it to see logs and metrics

## Testing After Deployment

1. **Install app on Device A** (or use emulator)
2. **Install app on Device B** (or use another emulator/real device)
3. **Create a report from Device A**
4. **Device B should receive notification within 5-10 seconds**

## Troubleshooting Deployment

### Error: "Firebase project not found"
**Solution**: 
```bash
firebase use --add
```
Select your project from the list.

### Error: "Functions directory not found"
**Solution**: Make sure you're in the project root directory and `functions/` folder exists.

### Error: "Permission denied"
**Solution**: Make sure you're logged in with an account that has Firebase admin access.

### Error: "Module not found"
**Solution**: 
```bash
cd functions
npm install
cd ..
firebase deploy --only functions
```

## What the Cloud Function Does

1. **Listens** for new documents in Firestore `reports` collection
2. **Triggers** automatically when a new report is created
3. **Sends** FCM notification to all users subscribed to "allUsers" topic
4. **Notification format**:
   - Title: "New Disaster Report"
   - Body: "{DisasterType} reported near your location"

## Important Notes

- ✅ **Cloud Function runs on Firebase servers** (not in your app)
- ✅ **Automatic**: No manual trigger needed
- ✅ **Scalable**: Handles thousands of users
- ✅ **Free tier**: 2 million invocations/month (more than enough for testing)

## After Deployment

Once deployed, the function will:
- Automatically trigger when ANY new report is created
- Send notifications to ALL users subscribed to "allUsers" topic
- Log all activity in Firebase Console → Functions → Logs

## Next Steps

1. Deploy the function (see steps above)
2. Test by creating a report
3. Check Firebase Console → Functions → Logs
4. Verify notification is received on other devices


