package com.ltsw.animo.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ltsw.animo.MainActivity
import com.ltsw.animo.R
import com.ltsw.animo.data.model.Activity
import com.ltsw.animo.data.model.ActivityType
import java.time.ZoneId

object NotificationHelper {

    private const val CHANNEL_ID_APPOINTMENTS = "appointments_channel"
    private const val CHANNEL_ID_MEDICATIONS = "medications_channel"
    private const val CHANNEL_ID_DAILY_SUMMARY = "daily_summary_channel"

    private const val CHANNEL_NAME_APPOINTMENTS = "Appointment Reminders"
    private const val CHANNEL_NAME_MEDICATIONS = "Medication Reminders"
    private const val CHANNEL_NAME_DAILY_SUMMARY = "Daily Summary"

    /**
     * Create notification channels (required for Android 8.0+)
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Appointments channel
            val appointmentsChannel = NotificationChannel(
                CHANNEL_ID_APPOINTMENTS,
                CHANNEL_NAME_APPOINTMENTS,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for upcoming appointments and vaccinations"
                enableVibration(true)
            }

            // Medications channel
            val medicationsChannel = NotificationChannel(
                CHANNEL_ID_MEDICATIONS,
                CHANNEL_NAME_MEDICATIONS,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for medication times"
                enableVibration(true)
            }

            // Daily summary channel
            val summaryChannel = NotificationChannel(
                CHANNEL_ID_DAILY_SUMMARY,
                CHANNEL_NAME_DAILY_SUMMARY,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily activity summary for your pets"
            }

            notificationManager.createNotificationChannel(appointmentsChannel)
            notificationManager.createNotificationChannel(medicationsChannel)
            notificationManager.createNotificationChannel(summaryChannel)
        }
    }

    /**
     * Check if notification permission is granted
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permission not required for older versions
        }
    }

    /**
     * Schedule a notification for an activity
     */
    fun scheduleActivityNotification(context: Context, activity: Activity) {
        if (!hasNotificationPermission(context)) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Schedule notification 1 hour before the activity
        val notificationTime = activity.dateTime.minusHours(1)
        val triggerTime = notificationTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000

        // Only schedule if the time is in the future
        if (triggerTime > System.currentTimeMillis()) {
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                action = "com.ltsw.animo.ACTIVITY_REMINDER"
                putExtra("activity_id", activity.id)
                putExtra("activity_title", activity.title)
                putExtra("activity_type", activity.type.name)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                activity.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Schedule exact alarm
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        }
    }

    /**
     * Cancel a scheduled notification
     */
    fun cancelActivityNotification(context: Context, activityId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = "com.ltsw.animo.ACTIVITY_REMINDER"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            activityId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    /**
     * Show immediate notification
     */
    fun showNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String,
        activityType: ActivityType
    ) {
        if (!hasNotificationPermission(context)) return

        val channelId = when (activityType) {
            ActivityType.APPOINTMENT, ActivityType.VACCINATION -> CHANNEL_ID_APPOINTMENTS
            ActivityType.MEDICATION -> CHANNEL_ID_MEDICATIONS
            else -> CHANNEL_ID_DAILY_SUMMARY
        }

        // Intent to open app when notification is clicked
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Check permission before notifying to avoid SecurityException
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }
    }

    /**
     * Schedule daily summary notification
     */
    fun scheduleDailySummary(context: Context, enable: Boolean) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = "com.ltsw.animo.DAILY_SUMMARY"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            999, // Unique ID for daily summary
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (enable) {
            // Schedule for 8 PM every day
            val calendar = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 20)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)

                // If time has passed today, schedule for tomorrow
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(java.util.Calendar.DAY_OF_YEAR, 1)
                }
            }

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } else {
            alarmManager.cancel(pendingIntent)
        }
    }
}

