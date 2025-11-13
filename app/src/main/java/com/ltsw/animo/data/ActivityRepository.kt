package com.ltsw.animo.data

import com.ltsw.animo.data.database.ActivityDao
import com.ltsw.animo.data.model.Activity
import kotlinx.coroutines.flow.Flow

class ActivityRepository(private val activityDao: ActivityDao) {
    // Get all activities from the DAO's Flow. The UI will collect this.
    val allActivities: Flow<List<Activity>> = activityDao.getAllActivities()

    // Use a suspend function to insert data without blocking the main thread.
    suspend fun insert(activity: Activity) {
        activityDao.insertActivity(activity)
    }

    // Delete an activity by its ID
    suspend fun deleteById(activityId: Long) {
        activityDao.deleteActivity(activityId)
    }

    // Get a single activity by ID
    suspend fun getById(activityId: Long): Activity? {
        return activityDao.getActivityById(activityId)
    }
}