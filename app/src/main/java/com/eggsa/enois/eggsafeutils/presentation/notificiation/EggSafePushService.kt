package com.eggsa.enois.eggsafeutils.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.eggsa.enois.EggSafeActivity
import com.eggsa.enois.R
import com.eggsa.enois.eggsafeutils.presentation.app.EggSafeApp
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private val CHANNEL_ID = "eggsafe_notifications"

class EggSafePushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                showNotification(it.title ?: "EggSafe", it.body ?: "", data = remoteMessage.data["url"])
            } else {
                showNotification(it.title ?: "EggSafe", it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            handleDataPayload(remoteMessage.data)
        }
    }

    private fun showNotification(title: String, message: String, data: String?) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "EggSafe Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Создаем intent для открытия MainActivity при клике на уведомление
        val intent = Intent(this, EggSafeActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
//            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.egg_safe_noti_icon)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun handleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}