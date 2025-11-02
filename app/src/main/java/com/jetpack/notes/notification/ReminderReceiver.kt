package com.jetpack.notes.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jetpack.notes.MainActivity
import com.jetpack.notes.R

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.getStringExtra("noteTitle") ?: "Note Reminder"
        val message = intent?.getStringExtra("noteDescription") ?: "You have a Reminder notes !!!"

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "notes_reminder_channel"
        val notificationId = System.currentTimeMillis().toInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                channelId,
                "Notes Reminder",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for your saved notes"
            }
            val notificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.quick_notes_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = context?.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
            if (permission != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // User denied notification permission
                Toast.makeText(context, "Notification permission is not granted. Please enable it in settings.",
                    Toast.LENGTH_LONG).show()
                return
            }
        }

        //  Show notification
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, notification)
        }
    }
}