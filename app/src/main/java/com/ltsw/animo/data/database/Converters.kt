package com.ltsw.animo.data.database

import androidx.room.TypeConverter
import com.ltsw.animo.data.model.ActivityType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Type converters for Room database to handle custom types.
 * Room doesn't know how to persist LocalDateTime and enums by default,
 * so we provide conversion methods.
 */
class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let {
            LocalDateTime.parse(it, formatter)
        }
    }

    @TypeConverter
    fun fromActivityType(activityType: ActivityType): String {
        return activityType.name
    }

    @TypeConverter
    fun toActivityType(activityTypeString: String): ActivityType {
        return ActivityType.valueOf(activityTypeString)
    }
}

