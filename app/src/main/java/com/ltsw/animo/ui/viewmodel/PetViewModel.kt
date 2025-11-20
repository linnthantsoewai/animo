package com.ltsw.animo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ltsw.animo.data.PetRepository
import com.ltsw.animo.data.model.Pet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PetViewModel(private val repository: PetRepository) : ViewModel() {

    // All pets from database
    val allPets: StateFlow<List<Pet>> = repository.allPets
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    // Currently selected pet
    private val _selectedPet = MutableStateFlow<Pet?>(null)
    val selectedPet: StateFlow<Pet?> = _selectedPet.asStateFlow()

    // First pet for initial display
    val firstPet: StateFlow<Pet?> = repository.firstPet
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    init {
        // Set the first pet as selected when available
        viewModelScope.launch {
            firstPet.collect { pet ->
                if (_selectedPet.value == null && pet != null) {
                    _selectedPet.value = pet
                }
            }
        }
    }

    // Select a pet to display
    fun selectPet(pet: Pet) {
        _selectedPet.value = pet
    }

    // Insert a new pet
    fun insert(pet: Pet) = viewModelScope.launch {
        val petId = repository.insert(pet)
        // Load the newly created pet and select it
        val newPet = repository.getById(petId)
        if (newPet != null) {
            _selectedPet.value = newPet
        }
    }

    // Update an existing pet
    fun update(pet: Pet) = viewModelScope.launch {
        repository.update(pet)
        // Update the selected pet if it's the one being updated
        if (_selectedPet.value?.id == pet.id) {
            _selectedPet.value = pet
        }
    }

    // Delete a pet
    fun delete(pet: Pet) = viewModelScope.launch {
        repository.delete(pet)
        // If deleted pet was selected, select another pet
        if (_selectedPet.value?.id == pet.id) {
            val pets = allPets.value
            _selectedPet.value = pets.firstOrNull { it.id != pet.id }
        }
    }

    // Delete a pet by ID
    fun deleteById(petId: Long) = viewModelScope.launch {
        repository.deleteById(petId)
        // If deleted pet was selected, select another pet
        if (_selectedPet.value?.id == petId) {
            val pets = allPets.value
            _selectedPet.value = pets.firstOrNull { it.id != petId }
        }
    }
}

// Factory for creating PetViewModel with repository
class PetViewModelFactory(private val repository: PetRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PetViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

