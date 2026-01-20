# Notification Troubleshooting Guide

## Issue: Not Receiving Push Notifications

If you're not receiving notifications after creating a report, follow these steps:

## Step 1: Check Notification Permission

### Android 13+ (API 33+)
- The app will automatically request notification permission on first launch
- If denied, go to: **Settings → Apps → Disaster Alert → Notifications** → Enable

### Check Permission Status:
1. Open the app
2. Check Logcat for: `"Notification permission already granted"` or `"Requesting notification permission"`

## Step 2: Verify FCM Token

### Check Logcat:
Look for these logs when app starts:
```
FCM: ✅ FCM Token: [your-token-here]
FCM: ✅ Successfully subscribed to 'allUsers' topic
```

### If token is missing:
- Check Firebase project configuration
- Verify `google-services.json` is correct
- Check internet connection

## Step 3: Verify Cloud Function is Deployed

### Check if Function is Deployed:
```bash
firebase functions:list
```

You should see: `sendReportNotification`

### If not deployed:
```bash
cd /path/to/project
firebase deploy --only functions
```

### Check Function Logs:
1. Go to [Firebase Console](https://console.firebase.google.com)
2. Select your project
3. Go to **Functions** → **Logs**
4. Create a test report
5. Check if you see: `"Successfully sent notification"`

## Step 4: Test Notification Manually

### From Firebase Console:
1. Go to **Cloud Messaging** → **Send test message**
2. Enter your FCM token (from Logcat)
3. Title: "Test Notification"
4. Body: "This is a test"
5. Click **Test**

### If test notification works:
- FCM is configured correctly
- Issue is with Cloud Function

### If test notification doesn't work:
- Check notification permission
- Check FCM token is valid
- Verify `google-services.json`

## Step 5: Verify Report is Saved

### Check Firestore:
1. Go to Firebase Console → **Firestore Database**
2. Check `reports` collection
3. Verify new report appears when you submit

### If report is not saved:
- Check Firestore rules
- Check internet connection
- Check app logs for errors

## Step 6: Check Cloud Function Logs

### After creating a report:
1. Go to Firebase Console → **Functions** → **Logs**
2. Look for:
   - `"Successfully sent notification"` ✅
   - `"Error sending notification"` ❌

### Common Errors:

#### Error: "Function not found"
**Solution**: Deploy the function
```bash
firebase deploy --only functions
```

#### Error: "Permission denied"
**Solution**: Check Firestore rules allow reads

#### Error: "Topic not found"
**Solution**: This is normal - topic is created automatically when first user subscribes

## Step 7: Verify App is Subscribed

### Check Logcat on App Start:
```
FCM: ✅ Successfully subscribed to 'allUsers' topic
```

### If subscription fails:
- Check internet connection
- Check Firebase configuration
- Verify FCM is enabled in Firebase Console

## Step 8: Test with Multiple Devices

1. Install app on Device A and Device B
2. Create report from Device A
3. Device B should receive notification
4. If Device B doesn't receive:
   - Check Device B is subscribed (see Logcat)
   - Check Device B has notification permission
   - Check Device B has internet connection

## Common Issues and Solutions

### Issue 1: Permission Denied
**Symptom**: No notification permission dialog appears
**Solution**: 
- Android 13+: Check Settings → Apps → Notifications
- Android 12-: Notifications work automatically

### Issue 2: Cloud Function Not Triggering
**Symptom**: Report saved but no notification
**Solution**:
1. Check function is deployed: `firebase functions:list`
2. Check Firestore rules allow writes
3. Check function logs in Firebase Console

### Issue 3: Notification Received but Not Displayed
**Symptom**: Logcat shows notification received but not visible
**Solution**:
- Check notification channel is created
- Check notification permission is granted
- Check device's "Do Not Disturb" mode

### Issue 4: FCM Token Not Generated
**Symptom**: No FCM token in Logcat
**Solution**:
- Verify `google-services.json` is correct
- Check Firebase project has FCM enabled
- Reinstall app

## Debug Checklist

- [ ] Notification permission granted (Android 13+)
- [ ] FCM token generated (check Logcat)
- [ ] Subscribed to "allUsers" topic (check Logcat)
- [ ] Cloud Function deployed (`firebase functions:list`)
- [ ] Report saved to Firestore (check Firebase Console)
- [ ] Cloud Function triggered (check Function Logs)
- [ ] Notification received in app (check Logcat: "Notification received!")
- [ ] Notification displayed (check device)

## Testing Steps

1. **Clean install app** (uninstall and reinstall)
2. **Grant notification permission** when prompted
3. **Check Logcat** for FCM token and subscription
4. **Create a test report**
5. **Check Firebase Console** → Functions → Logs
6. **Check device** for notification
7. **Check Logcat** for "Notification received!"

## Still Not Working?

1. **Check Firebase Console** → Project Settings → Cloud Messaging
   - Verify FCM is enabled
   - Check server key is present

2. **Verify google-services.json**:
   - File exists in `app/` directory
   - Contains correct project configuration
   - Matches Firebase project

3. **Check Firestore Rules**:
   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /reports/{reportId} {
         allow read, write: if request.auth != null;
       }
     }
   }
   ```

4. **Test Cloud Function Manually**:
   - Go to Firebase Console → Functions
   - Click on `sendReportNotification`
   - Check "Test" tab
   - Create a test document in Firestore

## Logcat Commands

### Filter FCM logs:
```bash
adb logcat | grep -E "(FCM|FCMService|MainActivity.*notification)"
```

### Filter Cloud Function logs (if testing locally):
```bash
firebase functions:log
```

## Contact Points

If issues persist:
1. Check Firebase Console for errors
2. Review Logcat for detailed error messages
3. Verify all setup steps in `PUSH_NOTIFICATION_SETUP.md`


