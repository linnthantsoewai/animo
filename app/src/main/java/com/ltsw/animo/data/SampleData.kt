package com.example.animo.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.animo.data.model.Activity
import com.example.animo.data.model.ActivityType
import com.example.animo.data.model.NotificationSettings
import com.example.animo.data.model.Pet
import java.time.LocalDateTime

// A singleton object to hold sample data for the application preview.
// In a real app, this would be replaced by a database (Room) and a repository.
object SampleData {

    // A sample list of pets
    val pets = mutableStateListOf(
        Pet(1, "Max", "Golden Retriever"),
        Pet(2, "Luna", "Siberian Husky")
    )

    // A sample list of activities and appointments
    val activities = mutableStateListOf(
        Activity(1, ActivityType.WALK, "Morning Walk", LocalDateTime.now().withHour(8).withMinute(0)),
        Activity(2, ActivityType.MEAL, "Breakfast", LocalDateTime.now().withHour(8).withMinute(30)),
        Activity(3, ActivityType.MEAL, "Dinner", LocalDateTime.now().withHour(18).withMinute(0)),
        Activity(4, ActivityType.APPOINTMENT, "Annual Check-up", LocalDateTime.of(2025, 10, 25, 10, 0)),
        Activity(5, ActivityType.VACCINATION, "Rabies Booster", LocalDateTime.of(2025, 11, 15, 14, 30)),
        Activity(6, ActivityType.WALK, "Evening Park Visit", LocalDateTime.of(2025, 10, 13, 19, 0))
    )

    // Sample notification settings
    val notificationSettings = mutableStateOf(NotificationSettings(true, true, false))
}
