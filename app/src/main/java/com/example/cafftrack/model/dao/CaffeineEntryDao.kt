package com.example.cafftrack.model.dao

import androidx.room.*
import com.example.cafftrack.model.entity.CaffeineEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CaffeineEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: CaffeineEntryEntity)

    @Delete
    suspend fun delete(entry: CaffeineEntryEntity)

    @Query("SELECT * FROM caffeine_entries ORDER BY timestamp DESC")
    fun getAll(): Flow<List<CaffeineEntryEntity>>
}