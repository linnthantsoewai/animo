package com.ltsw.animo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ltsw.animo.data.model.Activity
import com.ltsw.animo.data.model.NotificationSettings
import com.ltsw.animo.data.model.Pet

/**
 * The Room database for this app.
 * Defines the database configuration and serves as the main access point
 * for the underlying connection to your app's persisted data.
 */
@Database(entities = [Activity::class, Pet::class, NotificationSettings::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun petDao(): PetDao
    abstract fun notificationSettingsDao(): NotificationSettingsDao

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
                    .fallbackToDestructiveMigration() // Allow destructive migration for schema changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

