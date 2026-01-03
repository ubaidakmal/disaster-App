# Cloudinary Setup Guide

This guide will help you set up Cloudinary for image uploads in the Disaster Alert app.

## Step 1: Create a Cloudinary Account

1. Go to [https://cloudinary.com](https://cloudinary.com)
2. Click "Sign Up" and create a free account
3. Verify your email address

## Step 2: Get Your Cloudinary Credentials

1. After logging in, go to the [Dashboard](https://console.cloudinary.com/console)
2. You'll see your **Cloud Name**, **API Key**, and **API Secret**
3. Copy these values (you'll need them in Step 3)

## Step 3: Create an Upload Preset

1. In the Cloudinary Dashboard, go to **Settings** → **Upload** → **Upload presets**
2. Click **Add upload preset**
3. Configure the preset:
   - **Preset name**: Give it a name (e.g., `disaster_reports`)
   - **Signing mode**: Select **Unsigned** (for client-side uploads)
   - **Folder**: Set to `disaster_reports` (optional, for organization)
   - **Upload manipulation**: Leave default settings
4. Click **Save**

## Step 4: Configure the App

1. Open `app/src/main/java/com/bc230420212/app/util/CloudinaryHelper.kt`
2. Replace the placeholder values:

```kotlin
// Replace these with your actual Cloudinary credentials
private const val CLOUD_NAME = "YOUR_CLOUD_NAME"        // e.g., "dxyz1234"
private const val API_KEY = "YOUR_API_KEY"              // e.g., "123456789012345"
private const val API_SECRET = "YOUR_API_SECRET"        // e.g., "abcdefghijklmnopqrstuvwxyz"

// Replace with your upload preset name
const val UPLOAD_PRESET = "YOUR_UPLOAD_PRESET"          // e.g., "disaster_reports"
```

## Step 5: Test the Integration

1. Run the app
2. Go to **Report Disaster** screen
3. Fill in the disaster details
4. Click **Select Images** to choose photos from your device
5. Selected images will appear as thumbnails
6. Click **Submit Report**
7. Images will be uploaded to Cloudinary automatically
8. The Cloudinary URLs will be saved to Firestore

## How It Works

1. **User selects images**: The app opens the device gallery
2. **Images are stored locally**: Selected images are temporarily stored in the app's cache
3. **On submit**: 
   - Images are uploaded to Cloudinary one by one
   - Progress is shown to the user
   - Cloudinary returns secure URLs for each image
4. **Save to Firestore**: The report is saved with the Cloudinary image URLs in the `mediaUrls` field

## Image URLs in Firestore

The uploaded images will be stored in Firestore as an array of URLs:
```json
{
  "mediaUrls": [
    "https://res.cloudinary.com/YOUR_CLOUD_NAME/image/upload/v1234567890/disaster_reports/abc123.jpg",
    "https://res.cloudinary.com/YOUR_CLOUD_NAME/image/upload/v1234567890/disaster_reports/def456.jpg"
  ]
}
```

## Troubleshooting

### Upload Fails
- Check that your Cloudinary credentials are correct
- Verify the upload preset name matches exactly
- Ensure the upload preset is set to **Unsigned** mode
- Check your internet connection

### Images Not Showing
- Verify the URLs are saved correctly in Firestore
- Check that the image URLs are accessible
- Ensure Coil (image loading library) is properly configured

### Permission Errors
- Grant camera and storage permissions when prompted
- Go to app settings and manually grant permissions if needed

## Security Notes

⚠️ **Important**: For production apps, consider:
- Using signed uploads with server-side authentication
- Implementing upload size limits
- Adding image validation and moderation
- Using Cloudinary's security features

## Resources

- [Cloudinary Android SDK Documentation](https://cloudinary.com/documentation/android_quickstart)
- [Cloudinary Kotlin Integration](https://cloudinary.com/documentation/kotlin_integration)
- [Cloudinary Dashboard](https://console.cloudinary.com)

