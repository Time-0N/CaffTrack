package com.example.cafftrack.model

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cafftrack.model.dao.CaffeineEntryDao
import com.example.cafftrack.model.entity.CaffeineEntry

@Database(
    entities = [CaffeineEntry::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun caffeineEntryDao(): CaffeineEntryDao
}