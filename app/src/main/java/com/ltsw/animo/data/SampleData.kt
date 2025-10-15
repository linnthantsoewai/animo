package com.ltsw.animo.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.ltsw.animo.data.model.Activity
import com.ltsw.animo.data.model.ActivityType
import com.ltsw.animo.data.model.NotificationSettings
import com.ltsw.animo.data.model.Pet
import java.time.LocalDateTime

object SampleData {
    val pets = mutableStateListOf(
        Pet(1, "Max", "Golden Retriever"),
        Pet(2, "Luna", "Siberian Husky")
    )
    val activities = mutableStateListOf(
        Activity(1, ActivityType.WALK, "Morning Walk", LocalDateTime.now().withHour(8).withMinute(0)),
        Activity(2, ActivityType.MEAL, "Breakfast", LocalDateTime.now().withHour(8).withMinute(30)),
        Activity(3, ActivityType.MEAL, "Dinner", LocalDateTime.now().withHour(18).withMinute(0)),
        Activity(4, ActivityType.APPOINTMENT, "Annual Check-up", LocalDateTime.of(2025, 10, 25, 10, 0)),
        Activity(5, ActivityType.VACCINATION, "Rabies Booster", LocalDateTime.of(2025, 11, 15, 14, 30)),
        Activity(6, ActivityType.WALK, "Evening Park Visit", LocalDateTime.of(2025, 10, 13, 19, 0))
    )
    val notificationSettings = mutableStateOf(NotificationSettings(true, true, false))
}
