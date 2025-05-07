package com.example.cafftrack.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cafftrack.model.dao.CaffeineEntryDao
import com.example.cafftrack.model.dao.UserProfileDao
import com.example.cafftrack.model.entity.CaffeineEntryEntity
import com.example.cafftrack.model.entity.UserProfileEntity

@Database(
    entities = [CaffeineEntryEntity::class, UserProfileEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun caffeineEntryDao(): CaffeineEntryDao
    abstract fun userProfileDao(): UserProfileDao
}