package com.bc230420212.app.util

import android.content.Context
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.android.callback.UploadResult
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * CLOUDINARY HELPER
 * 
 * This helper class manages Cloudinary configuration and image uploads.
 * Based on Cloudinary Android SDK documentation:
 * https://cloudinary.com/documentation/android_quickstart
 * 
 * To use Cloudinary:
 * 1. Sign up at https://cloudinary.com
 * 2. Get your Cloud Name, API Key, and API Secret from the dashboard
 * 3. Update the configuration below with your credentials
 * 4. Create an unsigned upload preset in Cloudinary dashboard
 */
object CloudinaryHelper {
    
    // TODO: Replace these with your actual Cloudinary credentials
    // Get them from: https://console.cloudinary.com/settings/api-keys
    private const val CLOUD_NAME = "db0suczdx"
    private const val API_KEY = "196153192972686"
    private const val API_SECRET = "IC1Zoh0eKyBUhGJbzPR698OY_80"
    
    // TODO: Replace with your unsigned upload preset name
    // Create one at: https://console.cloudinary.com/settings/upload_presets
    // IMPORTANT: Preset name should NOT have spaces. Use underscore or hyphen instead.
    // If your preset has a space, either rename it in Cloudinary or use the exact name here.
    const val UPLOAD_PRESET = "disaster_app"  // Changed from "disaster App" - no spaces!
    
    /**
     * Initialize Cloudinary with your credentials
     * Call this in your Application class or MainActivity onCreate
     * 
     * For unsigned uploads, we only need cloud_name.
     * API key and secret are not required for unsigned upload presets.
     * 
     * @param context - Android application context
     */
    fun init(context: Context) {
        // For unsigned uploads, we need cloud_name, api_key, and api_secret
        // Note: Even though we're using unsigned upload presets, the Cloudinary Android SDK
        // requires api_secret during initialization for internal operations.
        // The actual upload will still use the unsigned preset (no signature required).
        val config = hashMapOf(
            "cloud_name" to CLOUD_NAME,
            "api_key" to API_KEY,
            "api_secret" to API_SECRET
        )
        
        android.util.Log.d("CloudinaryHelper", "Initializing Cloudinary with cloud_name: $CLOUD_NAME, api_key: $API_KEY")
        android.util.Log.d("CloudinaryHelper", "Upload preset: $UPLOAD_PRESET (unsigned)")
        
        MediaManager.init(context, config)
        
        android.util.Log.d("CloudinaryHelper", "Cloudinary initialized successfully")
    }
    
    /**
     * Upload an image file to Cloudinary
     * 
     * @param filePath - Path to the image file on device
     * @param publicId - Optional public ID for the uploaded image
     * @return Result containing the secure URL of the uploaded image, or error
     */
    suspend fun uploadImage(
        filePath: String,
        publicId: String? = null
    ): Result<String> = suspendCancellableCoroutine { continuation ->
        
        // For unsigned uploads, we only need upload_preset
        // Make sure the upload preset name doesn't have spaces or special characters
        val uploadPreset = UPLOAD_PRESET.trim()
        
        android.util.Log.d("CloudinaryHelper", "Starting upload with preset: $uploadPreset")
        android.util.Log.d("CloudinaryHelper", "File path: $filePath")
        
        val request = MediaManager.get()
            .upload(filePath)
            .option("upload_preset", uploadPreset)
            .option("folder", "disaster_reports") // Organize uploads in a folder
        
        // Add public_id if provided
        publicId?.let { request.option("public_id", it) }
        
        val requestId = request.callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    // Upload started
                }
                
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    // Upload progress (optional)
                }
                
                override fun onSuccess(requestId: String, resultData: Map<Any?, Any?>) {
                    // Upload successful - get the secure URL
                    val secureUrl = resultData["secure_url"] as? String
                    if (secureUrl != null) {
                        continuation.resume(Result.success(secureUrl))
                    } else {
                        continuation.resume(Result.failure(Exception("No URL returned from Cloudinary")))
                    }
                }
                
                override fun onError(requestId: String, error: ErrorInfo) {
                    // Upload failed
                    android.util.Log.e("CloudinaryHelper", "Upload error: ${error.description}")
                    android.util.Log.e("CloudinaryHelper", "Error code: ${error.code}")
                    continuation.resume(
                        Result.failure(
                            Exception("Cloudinary upload failed: ${error.description}")
                        )
                    )
                }
                
                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    // Retry upload (handled automatically by SDK)
                }
            })
            .dispatch()
        
        // Handle cancellation
        continuation.invokeOnCancellation {
            // Cancel upload if coroutine is cancelled
            MediaManager.get().cancelRequest(requestId)
        }
    }
}

