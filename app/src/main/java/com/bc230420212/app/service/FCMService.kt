package com.bc230420212.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bc230420212.app.MainActivity
import com.bc230420212.app.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * FCM SERVICE
 * 
 * This service handles Firebase Cloud Messaging (FCM) push notifications.
 * It receives notifications from the server and displays them to users.
 * 
 * When a new disaster report is created, the Cloud Function sends a notification
 * to all users, and this service displays it.
 */
class FCMService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        android.util.Log.d("FCMService", "✅ New FCM token generated: $token")
        // Token is automatically refreshed by Firebase
        // You can save this token to Firestore if needed for targeted notifications
        // For now, we'll use topic-based notifications (all users)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        android.util.Log.d("FCMService", "📨 Notification received!")
        android.util.Log.d("FCMService", "From: ${remoteMessage.from}")
        android.util.Log.d("FCMService", "Message ID: ${remoteMessage.messageId}")
        android.util.Log.d("FCMService", "Data: ${remoteMessage.data}")
        android.util.Log.d("FCMService", "Notification: ${remoteMessage.notification?.title} - ${remoteMessage.notification?.body}")

        // Check if message contains notification payload (preferred method)
        remoteMessage.notification?.let { notification ->
            android.util.Log.d("FCMService", "Processing notification payload")
            sendNotification(
                notification.title ?: "New Disaster Report",
                notification.body ?: "A new disaster report has been created",
                remoteMessage.data["disasterType"] ?: "",
                remoteMessage.data["reportId"] ?: ""
            )
            return
        }

        // Fallback: Check if message contains data payload
        if (remoteMessage.data.isNotEmpty()) {
            android.util.Log.d("FCMService", "Processing data payload")
            val title = remoteMessage.data["title"] ?: "New Disaster Report"
            val body = remoteMessage.data["body"] ?: "A new disaster report has been created"
            val disasterType = remoteMessage.data["disasterType"] ?: ""
            val reportId = remoteMessage.data["reportId"] ?: ""

            // Show notification
            sendNotification(title, body, disasterType, reportId)
        }
    }

    /**
     * Create and show a notification
     */
    private fun sendNotification(
        title: String,
        messageBody: String,
        disasterType: String,
        reportId: String
    ) {
        android.util.Log.d("FCMService", "🔔 Creating notification: $title - $messageBody")
        
        // Check notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!notificationManager.areNotificationsEnabled()) {
                android.util.Log.w("FCMService", "⚠️ Notifications are disabled by user")
                return
            }
        }
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // You can add extra data here if needed
            putExtra("reportId", reportId)
            putExtra("disasterType", disasterType)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert) // You can replace with custom icon
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        // Channel ID must match the one used in Cloud Function
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "disaster_alert_channel", // Must match Cloud Function channelId
                "Disaster Alert Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new disaster reports"
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
        android.util.Log.d("FCMService", "✅ Notification displayed with ID: $notificationId")
    }
}

