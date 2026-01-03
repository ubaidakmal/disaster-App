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

**Location:** `app/src/main/java/com/bc230420212/app/ui/screens/auth/LoginScreen.kt`

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

**Location:** `app/src/main/java/com/bc230420212/app/ui/screens/auth/RegisterScreen.kt`

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

### 3. Home Screen (Dashboard) (`HomeScreen.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/ui/screens/home/HomeScreen.kt`

**Screen Title:** Home Screen / Dashboard

**What This Screen Does:**
This is the main navigation screen users see after logging in. It serves as the dashboard with navigation cards/buttons to access all major features of the app.

**How It Works:**
1. **Top Bar (AppBar)**:
   - Shows app name "Disaster Alert"
   - Blue colored header
   - Displays user's role (USER/ADMIN) on the right

2. **Welcome Section**:
   - Displays "Welcome!" message
   - Shows subtitle "Stay safe, stay informed"

3. **Dashboard Cards** (Main Navigation):
   - **Report Disaster Card**: Navigate to report a new disaster
   - **View Reports Card**: Navigate to see list of all reports
   - **Map View Card**: Navigate to see reports on interactive map
   - **SOS Emergency Card**: Navigate to send emergency SOS alert
   - **Profile & Settings Card**: Navigate to user profile and settings

4. **Sign Out Button**:
   - Logs out the current user
   - Returns to Login Screen

**Features:**
- Clean card-based navigation design
- Each card has an icon and title
- Clicking a card navigates to the corresponding screen
- Scrollable layout for better UX
- Shows user role in the top bar

**Code Flow:**
```
User logged in → Display dashboard → User clicks a card → 
Navigate to corresponding screen → User can navigate back
```

**Components Used:**
- `DashboardCard` - Reusable card component for navigation
- `AppButton` - Reusable button for sign out
- Material3 Icons - For visual representation

---

## Components Documentation

### 1. AppButton Component (`AppButton.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/ui/components/AppButton.kt`

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

### 2. AppDropdown Component (`AppDropdown.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/ui/components/AppDropdown.kt`

**What It Does:**
A reusable dropdown menu component for selecting options. Used for disaster type selection in the Report Disaster screen.

**Features:**
- **Label**: Shows what the dropdown is for
- **Options**: List of options to choose from
- **Selected Option**: Shows currently selected value
- **Read-only**: User can only select from options, not type

**How to Use:**
```kotlin
AppDropdown(
    label = "Select Disaster Type",
    options = listOf("Flood", "Fire", "Earthquake"),
    selectedOption = selectedType,
    onOptionSelected = { newType -> /* update */ }
)
```

**Why We Created This:**
- Consistent dropdown design
- Easy to use throughout the app
- Prevents invalid input

---

### 3. AppTextArea Component (`AppTextArea.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/ui/components/AppTextArea.kt`

**What It Does:**
A reusable multi-line text input component for longer text like descriptions.

**Features:**
- **Multi-line**: Supports multiple lines of text
- **Height**: Fixed height (120dp) for better UX
- **Error Handling**: Can show error messages
- **Validation**: Built-in validation support

**How to Use:**
```kotlin
AppTextArea(
    value = description,
    onValueChange = { description = it },
    label = "Description",
    isError = false,
    errorMessage = "Required"
)
```

**Why We Created This:**
- Consistent text areas across the app
- Better for longer text input
- Easy to customize

---

### 4. ReportItem Component (`ReportItem.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/ui/components/ReportItem.kt`

**What It Does:**
A reusable card component that displays a single disaster report in a list. Shows all key information in a compact format.

**Features:**
- **Disaster Type**: Color-coded badge
- **Status**: Status badge (ACTIVE/RESOLVED/FALSE_ALARM)
- **Description**: Shortened to 2 lines with ellipsis
- **Time**: Formatted timestamp
- **Verification**: Confirmations and dismissals counts
- **Clickable**: Entire card is clickable

**How to Use:**
```kotlin
ReportItem(
    report = disasterReport,
    onClick = { /* navigate to details */ }
)
```

**Why We Created This:**
- Consistent list item design
- Shows all important info at a glance
- Easy to tap and navigate

---

### 5. SettingsItem Component (`SettingsItem.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/ui/components/SettingsItem.kt`

**What It Does:**
A reusable component for displaying settings options in the Profile screen. Shows an icon, title, optional subtitle, and trailing content.

**Features:**
- **Title**: Main text of the setting
- **Subtitle**: Optional subtitle text
- **Icon**: Optional icon on the left
- **Trailing Content**: Optional trailing content (e.g., switch, badge, arrow)
- **Clickable**: Entire item is clickable

**How to Use:**
```kotlin
SettingsItem(
    title = "Change Password",
    subtitle = "Update your account password",
    icon = Icons.Default.Lock,
    onClick = { /* action */ }
)
```

**With Switch:**
```kotlin
SettingsItem(
    title = "Notifications",
    icon = Icons.Default.Notifications,
    onClick = { /* toggle */ },
    trailingContent = {
        Switch(checked = enabled, onCheckedChange = { /* update */ })
    }
)
```

**Why We Created This:**
- Consistent settings UI across the app
- Easy to add new settings options
- Supports different trailing content types

---

### 6. DashboardCard Component (`DashboardCard.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/ui/components/DashboardCard.kt`

**What It Does:**
A reusable card component used in the Home Screen Dashboard for navigation. Each card represents a feature/function in the app.

**Features:**
- **Title**: Shows the feature name (e.g., "Report Disaster")
- **Icon**: Optional icon for visual representation
- **Clickable**: Entire card is clickable
- **Consistent Design**: All cards look the same

**How to Use:**
```kotlin
DashboardCard(
    title = "Report Disaster",
    icon = Icons.Default.Warning,
    onClick = { /* navigate */ }
)
```

**Why We Created This:**
- Consistent navigation cards across the dashboard
- Easy to add new features
- Clean and modern design
- Reusable component

---

### 7. AppTextField Component (`AppTextField.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/ui/components/AppTextField.kt`

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

### 8. AppColors (`AppColors.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/ui/theme/AppColors.kt`

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

### DisasterType Enum (`DisasterType.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/data/model/DisasterType.kt`

**What It Does:**
Defines all the types of disasters that can be reported in the app.

**Disaster Types:**
- FLOOD - Flood disasters
- FIRE - Fire emergencies
- EARTHQUAKE - Earthquake events
- ACCIDENT - Accidents
- STORM - Storms
- LANDSLIDE - Landslides
- OTHER - Other types of disasters

Each type has a `displayName` that is shown in the dropdown.

---

### DisasterReport Model (`DisasterReport.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/data/model/DisasterReport.kt`

**What It Does:**
Defines the structure of a disaster report in the app.

**Report Properties:**
- `id`: Unique report ID from Firestore
- `userId`: ID of user who created the report
- `disasterType`: Type of disaster (from DisasterType enum)
- `description`: Description of the disaster
- `latitude`: GPS latitude coordinate
- `longitude`: GPS longitude coordinate
- `address`: Human-readable address (optional)
- `mediaUrls`: List of photo/video URLs from Firebase Storage
- `timestamp`: When the report was created
- `status`: Report status (ACTIVE, RESOLVED, FALSE_ALARM)
- `confirmations`: Number of users who confirmed this report
- `dismissals`: Number of users who dismissed this report

**ReportStatus Enum:**
- `ACTIVE`: Report is active and needs attention
- `RESOLVED`: Report has been resolved
- `FALSE_ALARM`: Report was a false alarm

---

### SOSAlert Model (`SOSAlert.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/data/model/SOSAlert.kt`

**What It Does:**
Defines the structure of an SOS emergency alert in the app.

**Alert Properties:**
- `id`: Unique alert ID from Firestore
- `userId`: ID of user who sent the alert
- `userEmail`: Email of the user
- `userName`: Name of the user
- `latitude`: GPS latitude coordinate at time of alert
- `longitude`: GPS longitude coordinate at time of alert
- `address`: Human-readable address (optional)
- `timestamp`: When the alert was sent
- `status`: Alert status (ACTIVE, RESPONDED, RESOLVED)
- `message`: Optional message from the user

**SOSStatus Enum:**
- `ACTIVE`: Alert is active and needs response
- `RESPONDED`: Alert has been responded to
- `RESOLVED`: Alert has been resolved

---

### User Model (`User.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/data/model/User.kt`

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

### ReportsViewModel (`ReportsViewModel.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/ui/viewmodel/ReportsViewModel.kt`

**What It Does:**
Manages the state and logic for the View Reports and Report Details screens.

**State Management:**
- `isLoading`: Shows if reports are being loaded
- `errorMessage`: Stores any error messages
- `reports`: List of all disaster reports
- `selectedReport`: Currently selected report for details view

**Functions:**
1. **loadReports()**: Fetches all reports from Firestore
2. **loadReportById()**: Fetches a specific report by ID
3. **confirmReport()**: Increments confirmations count in Firestore
4. **dismissReport()**: Increments dismissals count in Firestore
5. **clearError()**: Removes error messages
6. **clearSelectedReport()**: Clears selected report

**How It Works:**
- Automatically loads reports when ViewModel is created
- Updates Firestore when user confirms/dismisses
- Refreshes data after updates
- Screens observe state changes and update UI

---

### ReportViewModel (`ReportViewModel.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/ui/viewmodel/ReportViewModel.kt`

**What It Does:**
Manages the state and logic for the Report Disaster screen.

**State Management:**
- `isLoading`: Shows if report is being submitted
- `errorMessage`: Stores any error messages
- `isSuccess`: True if report was submitted successfully
- `selectedDisasterType`: Currently selected disaster type
- `description`: Description text
- `latitude`, `longitude`: GPS coordinates
- `address`: Human-readable address
- `mediaUrls`: List of media file URLs

**Functions:**
1. **updateDisasterType()**: Updates selected disaster type
2. **updateDescription()**: Updates description text
3. **updateLocation()**: Updates GPS location and address
4. **submitReport()**: Validates and submits report to Firestore
5. **clearError()**: Removes error messages
6. **clearSuccess()**: Clears success state
7. **resetForm()**: Resets form to initial state

**How It Works:**
- Screens call ViewModel functions to update state
- ViewModel validates input
- ViewModel communicates with ReportRepository
- ViewModel updates state based on results
- Screens observe state changes and update UI

---

### AuthViewModel (`AuthViewModel.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/ui/viewmodel/AuthViewModel.kt`

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

**Location:** `app/src/main/java/com/bc230420212/app/data/repository/AuthRepository.kt`

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

### ReportRepository (`ReportRepository.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/data/repository/ReportRepository.kt`

**What It Does:**
Handles all Firestore operations related to disaster reports, including one-time voting system.

**Functions:**
1. **saveReport()**: Saves a new disaster report to Firestore
   - Converts report to HashMap format
   - Adds to "reports" collection
   - Returns report ID on success

2. **getAllReports()**: Fetches all reports from Firestore
   - Queries "reports" collection
   - Orders by timestamp (newest first)
   - Converts Firestore documents to DisasterReport objects

3. **getReportById()**: Fetches a single report by ID
   - Gets report document from Firestore
   - Returns DisasterReport object

4. **hasUserConfirmed()**: Checks if current user has already confirmed a report
   - Checks user ID in confirmedBy array
   - Returns true if user has voted
   - Used to prevent duplicate voting

5. **hasUserDismissed()**: Checks if current user has already dismissed a report
   - Checks user ID in dismissedBy array
   - Returns true if user has voted
   - Used to prevent duplicate voting

6. **confirmReport()**: Confirms a report (one-time per user)
   - Checks if user has already voted (confirmed or dismissed)
   - Adds user ID to confirmedBy array in Firestore
   - Updates confirmations count based on array size
   - Prevents duplicate voting
   - Returns error if user already voted

7. **dismissReport()**: Dismisses a report (one-time per user)
   - Checks if user has already voted (confirmed or dismissed)
   - Adds user ID to dismissedBy array in Firestore
   - Updates dismissals count based on array size
   - Prevents duplicate voting
   - Returns error if user already voted

**One-Time Voting System:**
- Each user can only vote once per report (either confirm or dismiss)
- User IDs are stored in `confirmedBy` and `dismissedBy` arrays in Firestore
- Counts are automatically calculated from array sizes
- Prevents vote manipulation and ensures fair verification
- UI buttons are disabled after user votes
- Shows status message if user has already voted

**Why We Use Repository:**
- Separates Firestore code from UI code
- Makes testing easier
- Can change Firestore implementation without changing screens

---

## Push Notifications (FCM)

### Overview

The app uses Firebase Cloud Messaging (FCM) to send push notifications to all users when a new disaster report is created.

### Architecture

**Client-Server Architecture:**

1. **Android App (Client)**:
   - User creates disaster report
   - Report is saved to Firestore
   - App subscribes to "allUsers" FCM topic
   - App receives and displays notifications

2. **Cloud Function (Server)**:
   - Automatically detects new report in Firestore
   - Sends FCM notification to "allUsers" topic
   - Runs on Firebase servers (not in app)

### How It Works

```
User creates report → Saved to Firestore → 
Cloud Function triggered → Sends FCM notification → 
All users receive notification
```

### Notification Content

- **Title**: "New Disaster Report"
- **Body**: "{DisasterType} reported near your location"
  - Example: "Fire reported near your location"
  - Example: "Flood reported near your location"

### Implementation Files

1. **FCMService.kt**: Handles receiving and displaying notifications
   - Location: `app/src/main/java/com/bc230420212/app/service/FCMService.kt`
   - Receives FCM messages
   - Creates notification channel
   - Displays notifications to users

2. **Cloud Function**: Sends notifications when reports are created
   - Location: `functions/index.js`
   - Automatically triggered on new report
   - Sends to "allUsers" topic

3. **MainActivity.kt**: Subscribes to notifications topic
   - Auto-subscribes to "allUsers" topic on app start

### Setup Instructions

See `FCM_SETUP.md` for detailed setup instructions.

**Quick Setup:**
1. Deploy Cloud Function: `firebase deploy --only functions`
2. App automatically subscribes to notifications
3. Test by creating a disaster report

### Important Points for HOD

- **App creates the report** → Saved to Firestore
- **Server sends notification** → Cloud Function detects new report and sends FCM notification
- **All users receive notification** → Via FCM topic subscription
- **Automatic process** → No manual intervention needed

---

## Navigation

### NavGraph (`NavGraph.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/ui/navigation/NavGraph.kt`

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

**Location:** `app/src/main/java/com/bc230420212/app/MainActivity.kt`

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
app/src/main/java/com/bc230420212/app/
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

## Additional Screens (Placeholders Created)

### 4. Report Disaster Screen (`ReportDisasterScreen.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/ui/screens/report/ReportDisasterScreen.kt`

**Screen Title:** Report Disaster

**What This Screen Does:**
This screen allows users to report a disaster with all necessary information. Users can select the disaster type, describe the situation, capture their location, and optionally upload media.

**How It Works:**
1. **Disaster Type Dropdown**:
   - User selects from: Flood, Fire, Earthquake, Accident, Storm, Landslide, or Other
   - Uses reusable `AppDropdown` component
   - Required field

2. **Description Text Box**:
   - Multi-line text area for describing the disaster
   - Uses reusable `AppTextArea` component
   - Required field (minimum validation)
   - Shows error if empty

3. **Location Section (GPS)**:
   - "Capture Location" button requests location permissions
   - Uses Google Play Services Location API
   - Captures GPS coordinates (latitude, longitude)
   - Attempts to get human-readable address using Geocoder
   - Shows captured location with coordinates/address
   - Required field - submit button disabled until location is captured

4. **Photo/Video Upload (Optional)**:
   - Placeholder section for media upload
   - Will be implemented to upload to Firebase Storage
   - Currently shows "Coming Soon" message
   - Button is disabled until implementation

5. **Submit Button**:
   - Validates all required fields
   - Saves report to Firestore database
   - Shows loading indicator while submitting
   - Shows success dialog on completion
   - Navigates back to home after success

**Key Features:**
- Form validation (description and location required)
- Permission handling for location and camera
- GPS location capture with address lookup
- Firestore integration for saving reports
- Success/error feedback
- Loading states

**Code Flow:**
```
User fills form → Click "Capture Location" → Request permissions → 
Get GPS coordinates → Enter description → Click "Submit Report" → 
Validate → Save to Firestore → Show success → Navigate back
```

**Data Saved to Firestore:**
- User ID (from Firebase Auth)
- Disaster type
- Description
- GPS coordinates (latitude, longitude)
- Address (if available)
- Timestamp
- Status (ACTIVE)
- Media URLs (when implemented)

**Status:** ✅ Fully Implemented with Firestore Integration

---

### 5. View Reports Screen (`ViewReportsScreen.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/ui/screens/reports/ViewReportsScreen.kt`

**Screen Title:** View Reports

**What This Screen Does:**
This screen displays a list of all disaster reports (both active and past) fetched from Firestore. Users can view reports, see verification info, and tap to see full details.

**How It Works:**
1. **Tab Navigation**:
   - **Active Tab**: Shows only active reports (status = ACTIVE)
   - **Past Tab**: Shows resolved and false alarm reports

2. **Report List Items**:
   Each item displays:
   - **Disaster Type**: Color-coded badge (Flood=Blue, Fire=Red, etc.)
   - **Status**: ACTIVE, RESOLVED, or FALSE_ALARM
   - **Short Description**: First 2 lines of description (truncated)
   - **Time**: Formatted date and time (e.g., "Dec 21, 2024 14:30")
   - **Verification Info**: 
     - ✓ Confirmations count (green)
     - ✗ Dismissals count (red)

3. **Empty States**:
   - Shows message if no reports found
   - Different messages for Active vs Past tabs

4. **Loading State**:
   - Shows loading spinner while fetching from Firestore

5. **Error Handling**:
   - Shows error message if fetch fails
   - Retry button to reload reports

6. **Tap to View Details**:
   - Tapping any report item navigates to Report Details screen

**Key Features:**
- Fetches reports from Firestore in real-time
- Filters by status (Active/Past)
- Color-coded disaster types
- Verification counts displayed
- Scrollable list with LazyColumn for performance
- Empty state handling

**Code Flow:**
```
Screen opens → ViewModel loads reports from Firestore → 
Display in list → User taps item → Navigate to Details
```

**Status:** ✅ Fully Implemented with Firestore Integration

---

### 6. Report Details Screen (`ReportDetailsScreen.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/ui/screens/reports/ReportDetailsScreen.kt`

**Screen Title:** Report Details

**What This Screen Does:**
This screen shows complete details of a selected disaster report. Users can view all information, see location, preview media, and verify the report.

**How It Works:**
1. **Full Report Details**:
   - **Disaster Type**: Large, color-coded title
   - **Status Badge**: Colored badge showing ACTIVE/RESOLVED/FALSE_ALARM
   - **Description**: Full description text (not truncated)
   - **Location**: 
     - Address (if available)
     - GPS coordinates
     - Map preview placeholder (ready for Google Maps integration)
   - **Media Preview**: 
     - Shows if media files exist
     - Placeholder for media gallery (ready for implementation)
   - **Report Information**:
     - Time submitted
     - Number of confirmations
     - Number of dismissals

2. **Verification Buttons** (only for ACTIVE reports):
   - **Confirm Button**: Increments confirmations count in Firestore
   - **Dismiss Button**: Increments dismissals count in Firestore
   - **One-Time Voting**: Each user can only vote once per report
   - Buttons are disabled after user has voted
   - Shows status message if user has already voted
   - Both buttons update the report in real-time

3. **Navigation**:
   - Back button returns to View Reports list
   - Automatically loads report when screen opens

**Key Features:**
- Loads report by ID from Firestore
- Displays all report information
- Map preview placeholder (ready for Google Maps)
- Media preview placeholder (ready for Firebase Storage)
- Confirm/Dismiss functionality with Firestore updates
- Color-coded status and disaster types
- Scrollable layout

**Code Flow:**
```
Screen opens with reportId → ViewModel loads report from Firestore → 
Display all details → User clicks Confirm/Dismiss → 
Update Firestore → Refresh display
```

**Status:** ✅ Fully Implemented with Firestore Integration

---

### 7. Map View Screen (`MapViewScreen.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/ui/screens/map/MapViewScreen.kt`

**Screen Title:** Map View

**What This Screen Does:**
This screen displays all disaster reports on an interactive Google Map. Users can see reports as markers, view details in info windows, and navigate the map.

**How It Works:**
1. **Google Maps Integration**:
   - Uses Google Maps SDK for Android
   - Displays interactive map with zoom and pan
   - API key configured in AndroidManifest.xml

2. **Report Markers**:
   - Each report is displayed as a marker on the map
   - Markers are color-coded by disaster type:
     - **Flood**: Blue
     - **Fire**: Red
     - **Earthquake**: Orange
     - **Accident**: Yellow
     - **Storm**: Violet
     - **Landslide**: Magenta
     - **Other**: Green

3. **Marker Info Windows**:
   - Tapping a marker shows an info window with:
     - **Type**: Disaster type name
     - **Time**: Formatted date and time
     - **Status**: ACTIVE, RESOLVED, or FALSE_ALARM
     - **Verification**: Confirmations and dismissals counts
     - **Description**: Short description (truncated if long)

4. **Camera Position**:
   - Defaults to a central location
   - Automatically adjusts to show first report when loaded
   - Users can zoom and pan to explore

5. **Loading States**:
   - Shows loading spinner while fetching reports
   - Shows error message if fetch fails
   - Shows empty state if no reports available

**Key Features:**
- Google Maps integration with API key
- Color-coded markers by disaster type
- Info windows with report details
- Real-time data from Firestore
- Interactive map navigation
- Automatic camera positioning

**Code Flow:**
```
Screen opens → ViewModel loads reports from Firestore → 
Display markers on map → User taps marker → 
Show info window with details
```

**Dependencies:**
- `maps-compose`: Google Maps Compose library
- `play-services-maps`: Google Play Services Maps

**Status:** ✅ Fully Implemented with Google Maps Integration

**Location:** `app/src/main/java/com/bc230420212/app/ui/screens/map/MapViewScreen.kt`

**What It Does:**
Placeholder screen for displaying disaster reports on an interactive map. Will show:
- Google Maps integration
- Markers for each disaster report
- Color-coded markers by disaster type
- Report details on marker click
- Current location tracking

**Status:** Placeholder created, ready for Google Maps integration

---

### 8. SOS Emergency Screen (`SOSScreen.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/ui/screens/sos/SOSScreen.kt`

**Screen Title:** SOS Emergency

**What This Screen Does:**
This screen allows users to send emergency SOS alerts with their live location. It provides a quick and easy way to request emergency assistance.

**How It Works:**
1. **Big SOS Button**:
   - Large, prominent circular SOS button (200dp)
   - Red color (ErrorColor) for high visibility
   - "SOS EMERGENCY" text
   - Always visible and easy to tap

2. **Live Location Display**:
   - Automatically gets user's current GPS location when screen opens
   - Shows live location coordinates
   - Displays address if available
   - Updates in real-time
   - Shows loading indicator while getting location

3. **Send SOS Alert Button**:
   - Large button below the SOS indicator
   - Sends SOS alert to Firebase system
   - Includes current location coordinates
   - Includes timestamp
   - Disabled until location is obtained

4. **Alert Information**:
   - User ID and email
   - GPS coordinates (latitude, longitude)
   - Address (if available)
   - Timestamp
   - Optional message

5. **Status Messages**:
   - Success message when alert is sent
   - Error message if sending fails
   - Loading indicator while sending

**Key Features:**
- Big, prominent SOS button for quick access
- Live location tracking using GPS
- Automatic location capture
- Firebase integration for storing alerts
- Real-time status feedback
- Emergency-focused UI design

**Code Flow:**
```
Screen opens → Get current location → Display location → 
User taps SOS button → Send alert to Firebase → 
Show success/error message
```

**Firestore Structure:**
SOS alerts are saved to the `sosAlerts` collection with:
- userId, userEmail, userName
- latitude, longitude, address
- timestamp, status (ACTIVE)
- message (optional)

**Status:** ✅ Fully Implemented with Firebase Integration

**Note:** Contacts integration is prepared (permissions added) but can be implemented later to send SMS/notifications to saved emergency contacts.

---

### 9. Profile & Settings Screen (`ProfileScreen.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/ui/screens/profile/ProfileScreen.kt`

**Screen Title:** Profile & Settings

**What This Screen Does:**
This screen displays user profile information and provides essential settings options for managing the app and account.

**How It Works:**
1. **Profile Header**:
   - Profile picture placeholder with user's initial
   - User's display name
   - User's email address
   - Role badge (USER/ADMIN) with color coding

2. **Account Section**:
   - **Change Password**: Opens dialog to change account password
     - Requires current password
     - New password confirmation
     - Password validation
     - Firebase authentication integration

3. **Settings Section**:
   - **Notifications**: Toggle switch for push notifications
     - Enable/disable notifications
     - Toggle switch UI
   - **Privacy Policy**: View privacy policy (placeholder)
   - **Terms of Service**: View terms and conditions (placeholder)

4. **About Section**:
   - **App Version**: Shows app version (1.0.0)
     - Opens About dialog with app information
   - **Help & Support**: Get help and contact support (placeholder)

5. **Sign Out Button**:
   - Large button at bottom
   - Signs out from Firebase
   - Returns to Login screen

**Key Features:**
- User profile display with role badge
- Change password functionality
- Notification settings toggle
- About dialog with app information
- Privacy and Terms links (ready for implementation)
- Clean, organized settings UI
- Scrollable layout

**Code Flow:**
```
Screen opens → Load user info from Firebase Auth → 
Display profile → User clicks settings option → 
Open dialog/perform action → Update Firebase if needed
```

**Status:** ✅ Fully Implemented with Essential Settings

**Components Used:**
- `SettingsItem` - Reusable settings option component
- `AppButton` - For sign out
- `ChangePasswordDialog` - Dialog for password change
- `AboutDialog` - Dialog showing app information

---

### 10. Admin Panel Screen (`AdminPanelScreen.kt`)

**Location:** `app/src/main/java/com/bc230420212/app/ui/screens/admin/AdminPanelScreen.kt`

**Screen Title:** Admin Panel

**What This Screen Does:**
This screen is **only accessible to ADMIN users**. It allows admins to review pending disaster reports and update their status (VERIFIED, RESOLVED, FALSE_ALARM).

**How It Works:**
1. **Admin Login**:
   - Admin logs in with their credentials
   - App checks user role in Firestore database
   - If role is ADMIN, Admin Panel option appears in Home Screen

2. **Admin Panel Access**:
   - Admin Panel card appears in Home Screen (only for ADMIN users)
   - Separated by a divider with "Admin Tools" section
   - Clicking opens Admin Panel screen

3. **Pending Reports List**:
   - Shows all reports with ACTIVE status
   - Displays report details:
     - Disaster type (color-coded)
     - Description
     - Timestamp
     - Confirmation/dismissal counts
     - Current status badge

4. **Status Update Actions**:
   - **Verify Button**: Updates status to VERIFIED
     - Green button with checkmark icon
     - Marks report as verified by admin
   - **Resolve Button**: Updates status to RESOLVED
     - Blue button with done icon
     - Marks report as resolved
   - **False Alarm Button**: Updates status to FALSE_ALARM
     - Red button with close icon
     - Marks report as false alarm

5. **Database Updates**:
   - Status update is saved to Firestore
   - All users see updated status in:
     - View Reports list
     - Map View markers
     - Report Details screen

**Key Features:**
- Role-based access control (ADMIN only)
- Real-time pending reports list
- Three status update options
- Success/error message display
- Automatic list refresh after status update
- Empty state when no pending reports
- Color-coded disaster types
- Timestamp formatting

**Code Flow:**
```
Admin logs in → App checks role → 
Admin Panel appears in Home → 
Admin opens Admin Panel → 
Load pending reports (ACTIVE status) → 
Admin clicks status button → 
Update Firestore → 
Refresh list → 
Users see updated status
```

**Status:** ✅ Fully Implemented with Role-Based Access

**Components Used:**
- `AdminReportCard` - Custom card for admin report management
- `AdminViewModel` - ViewModel for admin operations
- `ReportRepository.updateReportStatus()` - Repository function for status updates

**Repository Functions:**
- `getPendingReports()` - Fetches all ACTIVE reports
- `updateReportStatus()` - Updates report status in Firestore

**Status Values:**
- `ACTIVE` - Pending admin review (default for new reports)
- `VERIFIED` - Verified by admin
- `RESOLVED` - Resolved by admin
- `FALSE_ALARM` - Marked as false alarm by admin

---

## Future Features (To Be Implemented)

1. **Disaster Reporting**: Complete the report disaster functionality with Firestore integration
2. **Map View**: Integrate Google Maps and display reports as markers
3. **Report Verification**: Users can confirm or dismiss reports (crowdsourced verification)
4. **Admin Panel**: Admins can manage and verify reports
5. **SOS Feature**: Complete emergency alert with live location and contacts
6. **Push Notifications**: Real-time alerts for new disasters using Firebase Cloud Messaging
7. **Media Storage**: Upload and view disaster photos/videos using Firebase Storage

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

**Last Updated:** FCM Push Notifications Implementation (Android + Cloud Functions)
**Build Status:** ✅ All code errors fixed! (See BUILD_STATUS.md)
**Next Update:** Admin Panel Features

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

