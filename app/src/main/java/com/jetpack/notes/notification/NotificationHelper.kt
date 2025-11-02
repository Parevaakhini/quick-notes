package com.jetpack.notes.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast

class NotificationHelper {
    fun scheduleReminder(context: Context, timeInMillis: Long, title: String, message: String){
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message",message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                //  For Android 12 and above
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        timeInMillis,
                        pendingIntent
                    )
                    Toast.makeText(
                        context,
                        "Reminder set successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // ️ Ask the user to allow exact alarms in system settings
                    Toast.makeText(
                        context,
                        "Please allow exact alarm permission in settings",
                        Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }
            } else {
                // For Android 11 and below
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
                Toast.makeText(
                    context,
                    "Reminder set successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(
                context,
                "Permission denied to schedule exact alarm",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                context,
                "Failed to set reminder: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}