# Firebase Setup Instructions

## Prerequisites
1. Create a Firebase project at https://console.firebase.google.com/
2. Add an Android app to your Firebase project
3. Download `google-services.json` and place it in the `app/` folder

## Steps to Complete Setup

### 1. Add google-services.json (REQUIRED FOR BUILD)
- Download the `google-services.json` file from Firebase Console
- Place it in: `app/google-services.json`
- **IMPORTANT:** The app will NOT build without this file!
- See `app/google-services.json.placeholder` for reference

### 2. Enable Authentication Methods
In Firebase Console → Authentication → Sign-in method:
- Enable **Email/Password** authentication
- Enable **Google** sign-in provider
  - Add your app's SHA-1 fingerprint
  - Get the Web Client ID from the Google sign-in configuration

### 3. Update Google Sign-In Client ID
Open: `app/src/main/java/com/bc230420212/androidbasedcrowdsourceddisasteralertsafetyapp/util/GoogleSignInHelper.kt`

Replace `"YOUR_WEB_CLIENT_ID"` with your actual Web Client ID from Firebase Console.

### 4. Get SHA-1 Fingerprint (for Google Sign-In)
Run this command in terminal:
```bash
cd android
./gradlew signingReport
```
Copy the SHA-1 from the output and add it to Firebase Console → Project Settings → Your Android App → SHA certificate fingerprints

### 5. Enable Firestore Database
In Firebase Console → Firestore Database:
- Create a database in test mode (for development)
- The app will automatically create the `users` collection

## Testing
After completing the setup:
1. Build and run the app
2. Try registering a new user with email/password
3. Try signing in with Google
4. Check Firebase Console to verify user creation

