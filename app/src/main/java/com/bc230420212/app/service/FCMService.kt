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
        // Token is automatically refreshed by Firebase
        // You can save this token to Firestore if needed for targeted notifications
        // For now, we'll use topic-based notifications (all users)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if message contains data payload
        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"] ?: "New Disaster Report"
            val body = remoteMessage.data["body"] ?: "A new disaster report has been created"
            val disasterType = remoteMessage.data["disasterType"] ?: ""
            val reportId = remoteMessage.data["reportId"] ?: ""

            // Show notification
            sendNotification(title, body, disasterType, reportId)
        }

        // Check if message contains notification payload
        remoteMessage.notification?.let {
            sendNotification(
                it.title ?: "New Disaster Report",
                it.body ?: "A new disaster report has been created",
                "",
                ""
            )
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Disaster Alert Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new disaster reports"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}

