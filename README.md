# Android-based Crowdsourced Disaster Alert & Safety App

## Project Overview
This is a Final Year Project (FYP) that creates a mobile application for reporting and managing disaster alerts. The app allows users to report disasters in real-time, verify information, and receive instant alerts based on their location.

## Table of Contents
1. [Authentication System](#authentication-system)
2. [Project Structure](#project-structure)
3. [Screens Documentation](#screens-documentation)
4. [Components Documentation](#components-documentation)
5. [How to Run the Project](#how-to-run-the-project)

---

## Authentication System

### Overview
The authentication system allows users to register and login to the app using:
- **Email and Password** - Traditional email/password authentication
- **Google Sign-In** - Quick sign-in using Google account

The system also tracks user roles:
- **USER** - Regular users who can report disasters
- **ADMIN** - Administrators who can manage and verify reports

---

## Screens Documentation

### 1. Login Screen (`LoginScreen.kt`)

**Location:** `app/src/main/java/com/bc230420212/androidbasedcrowdsourceddisasteralertsafetyapp/ui/screens/auth/LoginScreen.kt`

**Screen Title:** Login Screen

**What This Screen Does:**
This is the first screen users see when they open the app. It allows existing users to sign in to their account.

**How It Works:**
1. **Email Field**: User enters their registered email address
   - Validates email format
   - Shows error if email is invalid or not found

2. **Password Field**: User enters their password
   - Password is hidden (shown as dots)
   - Shows error if password is incorrect

3. **Login Button**: 
   - When clicked, it sends the email and password to Firebase Authentication
   - Shows a loading indicator while processing
   - If successful, navigates to Home Screen
   - If failed, shows error message

4. **Google Sign-In Button**:
   - Opens Google sign-in dialog
   - User selects their Google account
   - Automatically signs in if successful

5. **Register Link**:
   - "Don't have an account? Register" link at the bottom
   - Clicking it navigates to Register Screen

**Key Features:**
- Input validation (checks if fields are not empty)
- Error handling (shows specific error messages)
- Loading state (shows spinner while processing)
- Automatic navigation on success

**Code Flow:**
```
User enters email/password → Click Login → AuthViewModel checks with Firebase → 
If valid → Navigate to Home → If invalid → Show error
```

---

### 2. Register Screen (`RegisterScreen.kt`)

**Location:** `app/src/main/java/com/bc230420212/androidbasedcrowdsourceddisasteralertsafetyapp/ui/screens/auth/RegisterScreen.kt`

**Screen Title:** Registration Screen

**What This Screen Does:**
This screen allows new users to create an account in the app.

**How It Works:**
1. **Full Name Field**: User enters their full name
   - Required field
   - Used as display name in the app

2. **Email Field**: User enters their email address
   - Must be a valid email format
   - Must be unique (not already registered)

3. **Password Field**: User creates a password
   - Minimum 6 characters required
   - Password is hidden for security

4. **Confirm Password Field**: User re-enters password
   - Must match the password field
   - Shows error if passwords don't match

5. **Register Button**:
   - Validates all fields
   - Creates new account in Firebase Authentication
   - Creates user profile in Firestore database
   - Sets default role as "USER"
   - Navigates to Home Screen on success

6. **Google Sign-In Button**:
   - Same as Login Screen
   - Creates account automatically if user doesn't exist

7. **Login Link**:
   - "Already have an account? Login" link
   - Navigates back to Login Screen

**Key Features:**
- Password confirmation validation
- Minimum password length check
- Automatic user profile creation
- Error messages for each field

**Code Flow:**
```
User fills all fields → Click Register → Validate inputs → Create Firebase account → 
Create Firestore user document → Navigate to Home
```

---

### 3. Home Screen (`HomeScreen.kt`)

**Location:** `app/src/main/java/com/bc230420212/androidbasedcrowdsourceddisasteralertsafetyapp/ui/screens/home/HomeScreen.kt`

**Screen Title:** Home Screen

**What This Screen Does:**
This is the main screen users see after logging in. Currently, it's a placeholder that shows:
- Welcome message
- User's role (USER or ADMIN)
- Sign out button

**How It Works:**
1. **Top Bar (AppBar)**:
   - Shows app name "Disaster Alert"
   - Blue colored header

2. **Welcome Section**:
   - Displays "Welcome!" message
   - Shows user's role (USER/ADMIN)

3. **Sign Out Button**:
   - Logs out the current user
   - Returns to Login Screen

**Note:** This screen will be expanded later to show:
- List of disaster reports
- Map view of disasters
- Quick report button
- SOS feature

**Code Flow:**
```
User logged in → Check authentication state → Display user info → 
User clicks Sign Out → Logout → Navigate to Login
```

---

## Components Documentation

### 1. AppButton Component (`AppButton.kt`)

**Location:** `app/src/main/java/com/bc230420212/androidbasedcrowdsourceddisasteralertsafetyapp/ui/components/AppButton.kt`

**What It Does:**
A reusable button component that can be used throughout the app. This ensures all buttons look consistent.

**Types of Buttons:**
1. **Primary Button** (`AppButton`):
   - Blue colored button
   - Used for main actions (Login, Register, etc.)
   - Full width, rounded corners

2. **Secondary Button** (`AppButton` with `isSecondary = true`):
   - Teal colored button
   - Used for alternative actions (Google Sign-In)

3. **Outlined Button** (`AppOutlinedButton`):
   - Button with border, no fill
   - Used for less important actions

**How to Use:**
```kotlin
AppButton(
    text = "Click Me",
    onClick = { /* action */ },
    enabled = true
)
```

**Why We Created This:**
- Makes code reusable (write once, use everywhere)
- Ensures consistent design
- Easy to modify button style in one place

---

### 2. AppTextField Component (`AppTextField.kt`)

**Location:** `app/src/main/java/com/bc230420212/androidbasedcrowdsourceddisasteralertsafetyapp/ui/components/AppTextField.kt`

**What It Does:**
A reusable text input field component for forms throughout the app.

**Features:**
- **Label**: Shows what the field is for (Email, Password, etc.)
- **Error Handling**: Can show error messages below the field
- **Password Mode**: Can hide text for password fields
- **Keyboard Types**: Automatically shows appropriate keyboard (email, number, etc.)

**How to Use:**
```kotlin
AppTextField(
    value = email,
    onValueChange = { email = it },
    label = "Email",
    keyboardType = KeyboardType.Email,
    isError = false,
    errorMessage = "Invalid email"
)
```

**Why We Created This:**
- Consistent input fields across the app
- Built-in validation support
- Easy to customize

---

### 3. AppColors (`AppColors.kt`)

**Location:** `app/src/main/java/com/bc230420212/androidbasedcrowdsourceddisasteralertsafetyapp/ui/theme/AppColors.kt`

**What It Does:**
A file that contains all color definitions used in the app. This is like a color palette.

**Color Categories:**
- **Primary Colors**: Main app color (Blue)
- **Secondary Colors**: Alternative color (Teal)
- **Status Colors**: Success (Green), Error (Red), Warning (Orange)
- **Text Colors**: For different text types
- **Disaster Type Colors**: Different colors for different disaster types

**Why We Created This:**
- All colors in one place - easy to change
- Consistent colors throughout the app
- Easy to understand what each color is for

**Example:**
```kotlin
// Instead of writing Color(0xFF2196F3) everywhere
// We write PrimaryColor
Button(colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor))
```

---

## Data Models

### User Model (`User.kt`)

**Location:** `app/src/main/java/com/bc230420212/androidbasedcrowdsourceddisasteralertsafetyapp/data/model/User.kt`

**What It Does:**
Defines the structure of user data in the app.

**User Properties:**
- `uid`: Unique user ID from Firebase
- `email`: User's email address
- `displayName`: User's full name
- `photoUrl`: Profile picture URL (for Google sign-in)
- `role`: Either USER or ADMIN

**UserRole Enum:**
- `USER`: Regular user who can report disasters
- `ADMIN`: Administrator who can manage reports

---

## ViewModels

### AuthViewModel (`AuthViewModel.kt`)

**Location:** `app/src/main/java/com/bc230420212/androidbasedcrowdsourceddisasteralertsafetyapp/ui/viewmodel/AuthViewModel.kt`

**What It Does:**
Manages the authentication state and handles all authentication operations.

**State Management:**
- `isLoading`: Shows if authentication is in progress
- `errorMessage`: Stores any error messages
- `isAuthenticated`: True if user is logged in
- `userRole`: Current user's role (USER/ADMIN)

**Functions:**
1. **signInWithEmail()**: Handles email/password login
2. **signUpWithEmail()**: Handles user registration
3. **signInWithGoogle()**: Handles Google sign-in
4. **signOut()**: Logs out the user
5. **clearError()**: Removes error messages

**How It Works:**
- Screens call ViewModel functions
- ViewModel communicates with AuthRepository
- ViewModel updates state
- Screens observe state changes and update UI

---

## Repositories

### AuthRepository (`AuthRepository.kt`)

**Location:** `app/src/main/java/com/bc230420212/androidbasedcrowdsourceddisasteralertsafetyapp/data/repository/AuthRepository.kt`

**What It Does:**
Handles all communication with Firebase Authentication and Firestore database.

**Functions:**
1. **signInWithEmail()**: Sends login request to Firebase
2. **signUpWithEmail()**: Creates new user in Firebase and Firestore
3. **signInWithGoogle()**: Handles Google authentication
4. **getUserRole()**: Gets user's role from Firestore
5. **signOut()**: Logs out from Firebase

**Why We Use Repository:**
- Separates Firebase code from UI code
- Makes testing easier
- Can change Firebase implementation without changing screens

---

## Navigation

### NavGraph (`NavGraph.kt`)

**Location:** `app/src/main/java/com/bc230420212/androidbasedcrowdsourceddisasteralertsafetyapp/ui/navigation/NavGraph.kt`

**What It Does:**
Manages navigation between different screens in the app.

**Screens:**
- `Login`: Login Screen
- `Register`: Registration Screen
- `Home`: Home Screen

**How Navigation Works:**
- Each screen has a route (like a URL)
- NavGraph defines all possible routes
- Screens can navigate to other screens using routes
- Navigation automatically handles back button

**Example:**
```kotlin
// Navigate from Login to Home
navController.navigate(Screen.Home.route) {
    popUpTo(Screen.Login.route) { inclusive = true }
}
// This removes Login from back stack, so user can't go back
```

---

## Main Activity

### MainActivity (`MainActivity.kt`)

**Location:** `app/src/main/java/com/bc230420212/androidbasedcrowdsourceddisasteralertsafetyapp/MainActivity.kt`

**What It Does:**
The main entry point of the app. It sets up the UI and handles Google Sign-In.

**Responsibilities:**
1. Sets up the app theme
2. Creates navigation controller
3. Determines start screen (Login or Home based on auth state)
4. Handles Google Sign-In result
5. Connects everything together

**Code Flow:**
```
App Starts → Check if user logged in → 
If logged in → Show Home → If not → Show Login
```

---

## Project Structure

```
app/src/main/java/com/bc230420212/androidbasedcrowdsourceddisasteralertsafetyapp/
├── MainActivity.kt                    # App entry point
├── data/
│   ├── model/
│   │   └── User.kt                   # User data structure
│   └── repository/
│       └── AuthRepository.kt         # Firebase operations
├── ui/
│   ├── components/
│   │   ├── AppButton.kt              # Reusable button
│   │   └── AppTextField.kt           # Reusable text field
│   ├── screens/
│   │   ├── auth/
│   │   │   ├── LoginScreen.kt        # Login screen
│   │   │   └── RegisterScreen.kt     # Registration screen
│   │   └── home/
│   │       └── HomeScreen.kt         # Home screen
│   ├── theme/
│   │   ├── AppColors.kt              # Color definitions
│   │   ├── Color.kt                  # Default colors
│   │   ├── Theme.kt                  # App theme
│   │   └── Type.kt                   # Typography
│   ├── viewmodel/
│   │   └── AuthViewModel.kt          # Authentication logic
│   └── navigation/
│       └── NavGraph.kt                # Navigation setup
└── util/
    └── GoogleSignInHelper.kt          # Google Sign-In helper
```

---

## How to Run the Project

### Prerequisites
1. Android Studio installed
2. Firebase project created
3. `google-services.json` file added to `app/` folder

### Steps
1. Open project in Android Studio
2. Wait for Gradle sync to complete
3. Connect Android device or start emulator
4. Click "Run" button (green play icon)
5. App will install and launch

### Firebase Setup
See `FIREBASE_SETUP.md` for detailed Firebase configuration instructions.

---

## Future Features (To Be Implemented)

1. **Disaster Reporting**: Users can report disasters with location and photos
2. **Map View**: Interactive map showing disaster locations
3. **Report Verification**: Users can confirm or dismiss reports
4. **Admin Panel**: Admins can manage and verify reports
5. **SOS Feature**: Emergency alert with live location
6. **Push Notifications**: Real-time alerts for new disasters
7. **Media Storage**: Upload and view disaster photos/videos

---

## Notes for Presentation

### Key Points to Explain:
1. **Two Authentication Methods**: Email/Password and Google Sign-In
2. **Role-Based System**: USER and ADMIN roles
3. **Reusable Components**: Buttons and TextFields for consistency
4. **Firebase Integration**: Secure authentication and data storage
5. **Clean Architecture**: Separated into Models, Repositories, ViewModels, and Screens

### Demo Flow:
1. Show Login Screen - explain fields
2. Register new user - show validation
3. Login with new account - show success
4. Show Home Screen - explain role display
5. Show Sign Out - return to Login

---

## Code Style Guidelines

### For Beginners:
- **All code is well-commented** - Every important section has comments explaining what it does
- **Variable names are descriptive** - Easy to understand what each variable stores
- **Functions do one thing only** - Each function has a single, clear purpose
- **Code is organized in logical folders** - Related files are grouped together
- **Reusable components reduce code duplication** - Write once, use everywhere

### Reading the Code:
When you open any file, you'll see:
1. **File-level comments** - Explains what the file/function does
2. **Inline comments** - Explains what each section does
3. **Parameter comments** - Explains what each parameter is for

Example:
```kotlin
/**
 * This function does something important
 * @param email - User's email address
 */
fun doSomething(email: String) {
    // Step 1: Validate email
    // Step 2: Process email
    // Step 3: Return result
}
```

### Best Practices Followed:
- Separation of concerns (UI, Logic, Data)
- State management with ViewModel
- Error handling
- Input validation
- Loading states

---

**Last Updated:** Authentication System Implementation (with detailed comments)
**Build Status:** ✅ All code errors fixed! (See BUILD_STATUS.md)
**Next Update:** Disaster Reporting Feature

---

## ⚠️ Important: Before Running the App

The app requires Firebase configuration to build and run:
1. **Add `google-services.json`** - Download from Firebase Console and place in `app/` folder
2. **Enable Firebase Authentication** - Enable Email/Password and Google Sign-In
3. **Update Google Client ID** - Replace `YOUR_WEB_CLIENT_ID` in `GoogleSignInHelper.kt`

**See `FIREBASE_SETUP.md` for detailed instructions.**

**See `BUILD_STATUS.md` for build status and fixed errors.**

---

## How to Read This Documentation

### For Your Presentation:
1. **Start with Project Overview** - Explain what the app does
2. **Show Screens Documentation** - Explain each screen step by step
3. **Explain Components** - Show how reusable components work
4. **Demo the Flow** - Walk through Login → Register → Home

### For Understanding the Code:
1. Read the README section for the file you're looking at
2. Open the actual code file
3. Read the comments in the code
4. Follow the code flow from top to bottom

### Tips:
- Each screen has a "How It Works" section explaining the flow
- Each component has examples showing how to use it
- Code comments explain what each line does
- Start with simple screens (Login) before complex ones

---

**Note:** This README will be updated every time a new feature is added. Always check here first to understand what was implemented!

