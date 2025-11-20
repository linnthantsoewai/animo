package com.ltsw.animo.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ltsw.animo.data.model.ActivityType

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "com.ltsw.animo.ACTIVITY_REMINDER" -> {
                val activityTitle = intent.getStringExtra("activity_title") ?: "Activity"
                val activityTypeString = intent.getStringExtra("activity_type") ?: "APPOINTMENT"
                val activityId = intent.getLongExtra("activity_id", 0)

                val activityType = try {
                    ActivityType.valueOf(activityTypeString)
                } catch (e: Exception) {
                    ActivityType.APPOINTMENT
                }

                val message = when (activityType) {
                    ActivityType.APPOINTMENT -> "Your appointment '$activityTitle' is in 1 hour"
                    ActivityType.VACCINATION -> "Vaccination reminder: $activityTitle in 1 hour"
                    ActivityType.MEDICATION -> "Time for medication: $activityTitle in 1 hour"
                    else -> "Reminder: $activityTitle in 1 hour"
                }

                NotificationHelper.showNotification(
                    context = context,
                    notificationId = activityId.toInt(),
                    title = "Pet Care Reminder",
                    message = message,
                    activityType = activityType
                )
            }

            "com.ltsw.animo.DAILY_SUMMARY" -> {
                NotificationHelper.showNotification(
                    context = context,
                    notificationId = 999,
                    title = "Daily Summary",
                    message = "Check your pet's activity summary for today",
                    activityType = ActivityType.WALK
                )
            }
        }
    }
}

