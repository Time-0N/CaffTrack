package com.example.cafftrack.repository

import com.example.cafftrack.model.dao.CaffeineEntryDao
import com.example.cafftrack.model.entity.CaffeineEntry
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CaffeineRepository @Inject constructor(
    private val dao: CaffeineEntryDao
) {
    fun getAllEntries(): Flow<List<CaffeineEntry>> = dao.getAll()

    suspend fun insert(entry: CaffeineEntry) = dao.insert(entry)

    suspend fun delete(entry: CaffeineEntry) = dao.delete(entry)
}