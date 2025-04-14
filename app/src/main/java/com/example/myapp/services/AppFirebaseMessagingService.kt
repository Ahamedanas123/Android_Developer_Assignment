package com.example.myapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.myapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class AppFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("Refreshed FCM token: $token")
        // Send this token to your server if needed
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle FCM messages here
        Timber.d("From: ${remoteMessage.from}")

        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            Timber.d("Notification Body: ${it.body}")
            showNotification(it.title ?: "New message", it.body ?: "")
        }

        // Check if message contains a data payload
        if (remoteMessage.data.isNotEmpty()) {
            Timber.d("Message data payload: ${remoteMessage.data}")
            // Handle data message here
        }
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "pdf_viewer_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since Android Oreo, notification channel is needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "PDF Viewer Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}