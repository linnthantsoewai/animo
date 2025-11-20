package com.ltsw.animo.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ltsw.animo.data.model.NotificationSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationSettingsDao {

    @Query("SELECT * FROM notification_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<NotificationSettings?>

    @Query("SELECT * FROM notification_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsOnce(): NotificationSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: NotificationSettings)

    @Update
    suspend fun updateSettings(settings: NotificationSettings)
}

