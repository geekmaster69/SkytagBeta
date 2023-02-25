package com.example.skytagbeta.base.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.skytagbeta.R
import com.example.skytagbeta.base.Constants.CHANNEL_ID
import com.example.skytagbeta.base.Constants.NOTIFICATION_ID
import com.example.skytagbeta.base.Constants.NOTIFICATION_TITLE
import com.example.skytagbeta.base.Constants.VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
import com.example.skytagbeta.base.Constants.VERBOSE_NOTIFICATION_CHANNEL_NAME

private const val TAG = "Workutils"
fun makeStatusNotification(message: String, context: Context, codding: Boolean){

    // Make a channel if necessary
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
    val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel(CHANNEL_ID, name, importance)
    channel.description = description

    // Add the channel
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

    notificationManager?.createNotificationChannel(channel)

    // Create the notification  SOS o Mensaje normal

    fun iconBuilder():NotificationCompat.Builder{
        if (codding) {
            return NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_emergency)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(LongArray(0))
        }else {
            return NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_click)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(LongArray(0))
        }
    }
    // Show the notification
    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, iconBuilder().build())
}


