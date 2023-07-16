package com.crazy_coder.everfit_wear.presentation

import android.R
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.annotation.NonNull


class WearNotificationManager private constructor(@NonNull context: Context) {
    private val appContext: Context
    private val notificationManager: NotificationManager
    private var streamNotification: Notification.Builder? = null
    private var statusNotification: Notification.Builder
    private var title: CharSequence? = null
    private var text: CharSequence? = null

    init {
        appContext = context.applicationContext
        notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        statusNotification = newStatusNotification("", "")
    }

    fun showStreamNotification(title: CharSequence, text: CharSequence) {
        streamNotification = newStreamNotification(title, text)
        notificationManager.notify(NOTIFICATION_ID_STREAM, streamNotification!!.build())
    }

    fun hideStreamNotification() {
        notificationManager.cancel(NOTIFICATION_ID_STREAM)
    }

    fun showStatusNotification(title: CharSequence, text: CharSequence) {
        statusNotification = newStatusNotification(title, text)
        notificationManager.notify(NOTIFICATION_ID_STATUS, statusNotification.build())
    }

    fun updateStatusNotification(title: CharSequence?, text: CharSequence?) {
        saveNotificationLabels(title, text)
        statusNotification.setContentTitle(title)
            .setContentText(text)
        notificationManager.notify(NOTIFICATION_ID_STATUS, statusNotification.build())
    }

    private fun newStreamNotification(
        title: CharSequence,
        text: CharSequence
    ): Notification.Builder {
        return Notification.Builder(appContext)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_delete)
            .setLocalOnly(true)
            .setPriority(Notification.PRIORITY_MAX)
            .setVibrate(longArrayOf(0, 50)) // Vibrate to bring card to top of stream
            .addAction(
                Notification.Action(
                    R.drawable.ic_delete,
                    "Fullscreen",
                    newLaunchFullscreenPendingIntent()
                )
            )

    }

    private fun newStatusNotification(
        contentTitle: CharSequence,
        contentText: CharSequence
    ): Notification.Builder {
        saveNotificationLabels(title, text)
        return Notification.Builder(appContext)
            .setContentTitle(contentTitle) // Current status and quick action icon
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_delete)
            .setLocalOnly(true)
            .setPriority(Notification.PRIORITY_MAX)
            .setVibrate(longArrayOf(0, 50)) // Vibrate to bring card to top of stream
            .extend(
                Notification.WearableExtender()
                    .setBackground(
                        BitmapFactory.decodeResource(
                            appContext.getResources(),
                            R.drawable.ic_delete
                        )
                    )
                    .addPage(
                        Notification.Builder(appContext)
                            .extend(
                                Notification.WearableExtender()
                                    .setDisplayIntent(newControlPendingIntent()) // On, Off, Start, Stop
                                    .setCustomSizePreset(Notification.WearableExtender.SIZE_FULL_SCREEN)
                            )
                            .build()
                    )
                    .addPage(
                        Notification.Builder(appContext)
                            .extend(
                                Notification.WearableExtender()
                                    .setDisplayIntent(newModePendingIntent()) // Mode wearable list view
                                    .setCustomSizePreset(Notification.WearableExtender.SIZE_FULL_SCREEN)
                            )
                            .build()
                    )
            )
    }

    private fun newLaunchFullscreenPendingIntent(): PendingIntent {
        val intent: Intent = Intent(appContext, MainActivity::class.java)
        return PendingIntent.getActivity(appContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun newControlPendingIntent(): PendingIntent {
        val displayIntent: Intent = Intent(
            appContext,
            MainActivity::class.java
        )
        return PendingIntent.getActivity(
            appContext,
            0,
            displayIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun newModePendingIntent(): PendingIntent {
        val displayIntent: Intent = Intent(appContext, MainActivity::class.java)
        return PendingIntent.getActivity(
            appContext,
            0,
            displayIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun saveNotificationLabels(title: CharSequence?, text: CharSequence?) {
        this.title = title
        this.text = text
    }

    companion object {
        private const val NOTIFICATION_ID_STATUS = 0
        private const val NOTIFICATION_ID_STREAM = 1
        private var instance: WearNotificationManager? = null
        fun from(@NonNull context: Context): WearNotificationManager? {
            if (instance == null) {
                instance = WearNotificationManager(context)
            }
            return instance
        }
    }
}