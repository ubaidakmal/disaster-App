# Cloudinary Upload Error - 401 Invalid Signature

## Error Message
```
Failed to upload image: Cloudinary upload failed: Server returned unexpected status code - 401 - 
{"error":{"message":"Invalid Signature ... String to sign - 'folder=disaster_reports&timestamp=...&upload_preset=disaster App'."}}
```

## Common Causes and Solutions

### 1. Upload Preset Name Has Spaces ❌
**Problem:** Your upload preset name is `"disaster App"` (has a space)

**Solution:**
1. Go to Cloudinary Dashboard → Settings → Upload → Upload presets
2. Either:
   - **Option A (Recommended):** Rename the preset to remove the space (e.g., `disaster_app` or `disaster-app`)
   - **Option B:** Update the code to use the exact preset name with the space

3. Update `CloudinaryHelper.kt`:
   ```kotlin
   const val UPLOAD_PRESET = "disaster_app"  // No spaces!
   ```

### 2. Upload Preset Not Set to "Unsigned" Mode ❌
**Problem:** The upload preset is set to "Signed" mode, which requires API secret and signature generation.

**Solution:**
1. Go to Cloudinary Dashboard → Settings → Upload → Upload presets
2. Click on your upload preset
3. Under **Signing mode**, select **Unsigned**
4. Click **Save**

### 3. Using API Secret for Unsigned Uploads ❌
**Problem:** The code is trying to use API secret for unsigned uploads.

**Solution:**
- For unsigned uploads, you only need:
  - Cloud Name
  - Upload Preset Name
- API Key and API Secret are NOT needed for unsigned uploads
- The updated code now handles this automatically

## Step-by-Step Fix

### Step 1: Fix Upload Preset Name
1. Open Cloudinary Dashboard: https://console.cloudinary.com
2. Go to **Settings** → **Upload** → **Upload presets**
3. Find your preset `"disaster App"`
4. Click **Edit**
5. Change the name to `disaster_app` (no spaces, use underscore)
6. Make sure **Signing mode** is set to **Unsigned**
7. Click **Save**

### Step 2: Update Code
1. Open `app/src/main/java/com/bc230420212/app/util/CloudinaryHelper.kt`
2. Update the upload preset name:
   ```kotlin
   const val UPLOAD_PRESET = "disaster_app"  // Match the name in Cloudinary
   ```

### Step 3: Verify Configuration
Your `CloudinaryHelper.kt` should have:
```kotlin
private const val CLOUD_NAME = "db0suczdx"
private const val API_KEY = "196153192972686"  // Optional for unsigned
private const val API_SECRET = "..."  // Not needed for unsigned, can be empty
const val UPLOAD_PRESET = "disaster_app"  // No spaces!
```

### Step 4: Test
1. Rebuild the app
2. Try uploading an image again
3. Check Logcat for: `"Upload started with preset: disaster_app"`

## Alternative: Create a New Upload Preset

If you want to start fresh:

1. Go to Cloudinary Dashboard → Settings → Upload → Upload presets
2. Click **Add upload preset**
3. Configure:
   - **Preset name:** `disaster_reports` (no spaces!)
   - **Signing mode:** **Unsigned** (important!)
   - **Folder:** `disaster_reports` (optional)
   - **Upload manipulation:** Leave default
4. Click **Save**
5. Update code:
   ```kotlin
   const val UPLOAD_PRESET = "disaster_reports"
   ```

## Verification Checklist

- [ ] Upload preset name has NO spaces
- [ ] Upload preset is set to **Unsigned** mode
- [ ] Code uses the exact preset name (case-sensitive)
- [ ] Cloud Name is correct
- [ ] Rebuilt the app after changes

## Still Having Issues?

1. **Check Logcat** for detailed error messages
2. **Verify preset name** matches exactly (case-sensitive)
3. **Test in Cloudinary Dashboard** - try uploading manually to verify preset works
4. **Check preset settings** - ensure it's unsigned and active

