package ch.timeon.cafftrack.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ch.timeon.cafftrack.model.dao.CaffeineEntryDao
import ch.timeon.cafftrack.model.dao.UserProfileDao
import ch.timeon.cafftrack.model.entity.CaffeineEntryEntity
import ch.timeon.cafftrack.model.entity.UserProfileEntity

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