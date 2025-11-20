package com.ltsw.animo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ltsw.animo.data.ActivityRepository
import com.ltsw.animo.data.model.Activity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ActivityViewModel(private val repository: ActivityRepository) : ViewModel() {

    // Current selected pet ID to filter activities
    private val _selectedPetId = MutableStateFlow<Long?>(null)

    // Expose selected pet ID as StateFlow
    val selectedPetId: StateFlow<Long?> = _selectedPetId

    // Use StateFlow to hold the list of activities from the database.
    // This will automatically update when selectedPetId changes.
    val allActivities: StateFlow<List<Activity>> = _selectedPetId
        .flatMapLatest { petId ->
            if (petId != null) {
                repository.getActivitiesByPetId(petId)
            } else {
                repository.allActivities
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    // Set the selected pet to filter activities
    fun setSelectedPet(petId: Long?) {
        _selectedPetId.value = petId
    }

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