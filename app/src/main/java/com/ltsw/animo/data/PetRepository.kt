package com.ltsw.animo.data

import com.ltsw.animo.data.database.PetDao
import com.ltsw.animo.data.model.Pet
import kotlinx.coroutines.flow.Flow

class PetRepository(private val petDao: PetDao) {
    // Get all pets from the database
    val allPets: Flow<List<Pet>> = petDao.getAllPets()

    // Get the first pet (for initial display)
    val firstPet: Flow<Pet?> = petDao.getFirstPet()

    // Insert a new pet and return its ID
    suspend fun insert(pet: Pet): Long {
        return petDao.insertPet(pet)
    }

    // Update an existing pet
    suspend fun update(pet: Pet) {
        petDao.updatePet(pet)
    }

    // Delete a pet
    suspend fun delete(pet: Pet) {
        petDao.deletePet(pet)
    }

    // Delete a pet by ID
    suspend fun deleteById(petId: Long) {
        petDao.deletePetById(petId)
    }

    // Get a single pet by ID
    suspend fun getById(petId: Long): Pet? {
        return petDao.getPetById(petId)
    }

    // Get the count of pets
    suspend fun getPetCount(): Int {
        return petDao.getPetCount()
    }
}

