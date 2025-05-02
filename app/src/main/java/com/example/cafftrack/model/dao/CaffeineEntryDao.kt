package com.example.cafftrack.model.dao

import androidx.room.*
import com.example.cafftrack.model.entity.CaffeineEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface CaffeineEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: CaffeineEntry)

    @Delete
    suspend fun delete(entry: CaffeineEntry)

    @Query("SELECT * FROM caffeine_entries ORDER BY timestamp DESC")
    fun getAll(): Flow<List<CaffeineEntry>>
}