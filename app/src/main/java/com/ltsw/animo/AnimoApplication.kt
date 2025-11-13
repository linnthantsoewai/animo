package com.ltsw.animo

import android.app.Application
import com.ltsw.animo.data.ActivityRepository
import com.ltsw.animo.data.database.AppDatabase

// This Application class will be the central owner of our database and repository.
class AnimoApplication : Application() {
    // Using 'lazy' means the database and repository are only created when they're first needed.
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { ActivityRepository(database.activityDao()) }
}