package com.crazy_coder.everfit_wear.presentation

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.crazy_coder.everfit_wear.R
import javax.inject.Inject

class AppNotificationManager constructor(
    private val applicationContext: Context,
) {
    private val notificationManager by lazy(LazyThreadSafetyMode.NONE) {
        NotificationManagerCompat.from(applicationContext)
    }

    fun showNotification(contentText: String) {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(
                NOTIFICATION_ID,
                createNotification(contentText)
            )
        }
    }


    fun cancelNotification() = notificationManager.cancel(NOTIFICATION_ID)

    fun createNotification(contentText: String): Notification {
        fun createNotificationChannelIfAboveAndroidO() {
            NotificationChannel(
                "notification_call_channel_id",
                "notification_call_channel_name",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "notification_call_channel_description"
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                setSound(null, null)
            }.let { notificationManager.createNotificationChannel(it) }
        }

        createNotificationChannelIfAboveAndroidO()

        val pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT.let {
            it or PendingIntent.FLAG_IMMUTABLE
        }
        return NotificationCompat.Builder(
            applicationContext,
            "notification_call_channel_id"
        )
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_heart)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSound(null)
            .setSilent(true)
            .setVibrate(null)
            .setContentIntent(
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    Intent(applicationContext, MainActivity::class.java),
                    pendingIntentFlags
                )
            ).build()
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_ACTION = "NOTIFICATION_ACTION"
        const val STOP_SERVICE = "STOP_SERVICE"
    }
}
