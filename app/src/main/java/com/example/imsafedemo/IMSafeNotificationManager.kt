package com.example.imsafedemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlin.random.Random

class IMSafeNotificationManager(val ctx:Context) {
    lateinit var notificationManager: NotificationManager
    lateinit var notificationBuilder: NotificationCompat.Builder
    var notificationId: Int = 0
    var channelId = "1"

    init {
        notificationManager =
            ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun createNotification(
        title: String,
        messageBody: String,
        subText: String,
        intent: Intent? = null
    ) {

        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            ctx, Random.nextInt(20000) /* Request code */, intent,0
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        notificationBuilder = NotificationCompat.Builder(ctx, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(ctx,R.color.colorPrimary))
            .setSubText(subText)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
    }

    fun updateNotification(contetTitle: String, subText: String) {

        notificationBuilder?.setContentTitle(contetTitle)
        notificationBuilder?.setSubText(subText)
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    fun notify(id: Int) {
        notificationId = id
        notificationManager.notify(
            id,
            notificationBuilder.build()
        )
    }

    fun createNotificationChannel(channel: NotificationChannel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager?.cancel(notificationId)
    }
}