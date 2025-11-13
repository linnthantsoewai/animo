package com.ltsw.animo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ltsw.animo.data.model.Activity
import com.ltsw.animo.data.model.ActivityType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * The Room database for this app.
 * Defines the database configuration and serves as the main access point
 * for the underlying connection to your app's persisted data.
 */
@Database(entities = [Activity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // If the INSTANCE is not null, return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "animo_database"
                )
                    .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.activityDao())
                    }
                }
            }
        }

        private suspend fun populateDatabase(activityDao: ActivityDao) {
            // Add sample activities
            val sampleActivities = listOf(
                Activity(
                    type = ActivityType.WALK,
                    title = "Morning Walk",
                    dateTime = LocalDateTime.now().withHour(8).withMinute(0).withSecond(0).withNano(0)
                ),
                Activity(
                    type = ActivityType.MEAL,
                    title = "Breakfast",
                    dateTime = LocalDateTime.now().withHour(8).withMinute(30).withSecond(0).withNano(0)
                ),
                Activity(
                    type = ActivityType.MEAL,
                    title = "Dinner",
                    dateTime = LocalDateTime.now().withHour(18).withMinute(0).withSecond(0).withNano(0)
                ),
                Activity(
                    type = ActivityType.APPOINTMENT,
                    title = "Annual Check-up",
                    dateTime = LocalDateTime.of(2025, 10, 25, 10, 0)
                ),
                Activity(
                    type = ActivityType.VACCINATION,
                    title = "Rabies Booster",
                    dateTime = LocalDateTime.of(2025, 11, 15, 14, 30)
                ),
                Activity(
                    type = ActivityType.WALK,
                    title = "Evening Park Visit",
                    dateTime = LocalDateTime.of(2025, 11, 13, 19, 0)
                )
            )

            sampleActivities.forEach { activity ->
                activityDao.insertActivity(activity)
            }
        }
    }
}

