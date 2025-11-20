package com.ltsw.animo.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ltsw.animo.data.model.Activity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities ORDER BY dateTime ASC")
    fun getAllActivities(): Flow<List<Activity>>

    @Query("SELECT * FROM activities WHERE petId = :petId ORDER BY dateTime ASC")
    fun getActivitiesByPetId(petId: Long): Flow<List<Activity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: Activity)

    @Query("DELETE FROM activities WHERE id = :activityId")
    suspend fun deleteActivity(activityId: Long)

    @Query("SELECT * FROM activities WHERE id = :activityId")
    suspend fun getActivityById(activityId: Long): Activity?
}

