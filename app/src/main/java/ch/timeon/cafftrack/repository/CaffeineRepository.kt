package ch.timeon.cafftrack.repository

import ch.timeon.cafftrack.model.dao.CaffeineEntryDao
import ch.timeon.cafftrack.model.entity.CaffeineEntryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CaffeineRepository @Inject constructor(
    private val dao: CaffeineEntryDao
) {
    fun getAllEntries(): Flow<List<CaffeineEntryEntity>> = dao.getAll()

    suspend fun insert(entry: CaffeineEntryEntity) = dao.insert(entry)

    suspend fun delete(entry: CaffeineEntryEntity) = dao.delete(entry)
}