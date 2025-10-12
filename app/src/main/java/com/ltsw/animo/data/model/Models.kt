package com.example.animo.data.model

import java.time.LocalDateTime

// Defines the structure for a pet
data class Pet(
    val id: Int,
    var name: String,
    var breed: String
)

// Defines the structure for a scheduled activity or event
data class Activity(
    val id: Long,
    val type: ActivityType,
    var title: String,
    var dateTime: LocalDateTime
)

// An enumeration for the different types of activities
enum class ActivityType {
    WALK, MEAL, MEDICATION, APPOINTMENT, VACCINATION
}

// Defines the structure for notification preferences
data class NotificationSettings(
    var appointments: Boolean,
    var medications: Boolean,
    var summary: Boolean
)
