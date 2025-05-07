package com.example.cafftrack.repository

import com.example.cafftrack.model.dao.CaffeineEntryDao
import com.example.cafftrack.model.entity.CaffeineEntryEntity
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