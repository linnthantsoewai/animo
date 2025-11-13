package com.ltsw.animo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

// Defines the structure for a pet
data class Pet(
    val id: Int,
    var name: String,
    var breed: String
)

// Defines the structure for a scheduled activity or event
@Entity(tableName = "activities")
data class Activity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
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
