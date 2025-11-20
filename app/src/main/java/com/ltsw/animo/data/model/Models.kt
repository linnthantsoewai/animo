package com.ltsw.animo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

// Defines the structure for a pet
@Entity(tableName = "pets")
data class Pet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var name: String,
    var breed: String,
    var age: Int = 0, // in years
    var weight: Double = 0.0, // in lbs
    var sex: String = "", // Male, Female, Unknown
    var imageUri: String = "", // Path to pet image (optional)
    var color: String = "", // Pet's color
    var microchipId: String = "", // Microchip ID (optional)
    var allergies: String = "", // Comma-separated allergies
    var medications: String = "", // Current medications
    var vetName: String = "", // Veterinarian name
    var vetPhone: String = "", // Veterinarian phone
    var notes: String = "" // Additional notes
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
