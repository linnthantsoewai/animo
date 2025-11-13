package com.ltsw.animo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ltsw.animo.data.ActivityRepository
import com.ltsw.animo.data.model.Activity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ActivityViewModel(private val repository: ActivityRepository) : ViewModel() {

    // Use StateFlow to hold the list of activities from the database.
    // This will automatically update the UI when the data changes.
    val allActivities: StateFlow<List<Activity>> = repository.allActivities
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    // Launch a coroutine to insert the data in a non-blocking way.
    fun insert(activity: Activity) = viewModelScope.launch {
        repository.insert(activity)
    }

    // Delete an activity by its ID
    fun deleteById(activityId: Long) = viewModelScope.launch {
        repository.deleteById(activityId)
    }
}

// This is a factory class that tells our app how to create the ViewModel,
// since it needs the repository as a parameter.
class ActivityViewModelFactory(private val repository: ActivityRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActivityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}