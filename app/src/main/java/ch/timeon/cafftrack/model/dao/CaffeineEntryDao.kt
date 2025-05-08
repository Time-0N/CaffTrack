package ch.timeon.cafftrack.model.dao

import androidx.room.*
import ch.timeon.cafftrack.model.entity.CaffeineEntryEntity
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