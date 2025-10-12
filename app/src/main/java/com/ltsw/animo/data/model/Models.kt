import java.time.LocalDateTime

data class Pet(val id: Int, var name: String, var breed: String)
data class Activity(val id: Long, val type: ActivityType, var title: String, var dateTime: LocalDateTime)
enum class ActivityType { WALK, MEAL, MEDICATION, APPOINTMENT, VACCINATION }
data class NotificationSettings(var appointments: Boolean, var medications: Boolean, var summary: Boolean)