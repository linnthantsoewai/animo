package com.ltsw.animo.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ltsw.animo.data.model.Pet
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
    @Query("SELECT * FROM pets ORDER BY name ASC")
    fun getAllPets(): Flow<List<Pet>>

    @Query("SELECT * FROM pets WHERE id = :petId")
    suspend fun getPetById(petId: Long): Pet?

    @Query("SELECT * FROM pets ORDER BY id ASC LIMIT 1")
    fun getFirstPet(): Flow<Pet?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: Pet): Long

    @Update
    suspend fun updatePet(pet: Pet)

    @Delete
    suspend fun deletePet(pet: Pet)

    @Query("DELETE FROM pets WHERE id = :petId")
    suspend fun deletePetById(petId: Long)

    @Query("SELECT COUNT(*) FROM pets")
    suspend fun getPetCount(): Int
}

