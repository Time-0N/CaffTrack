package com.example.cafftrack.di

import android.content.Context
import androidx.room.Room
import com.example.cafftrack.model.AppDatabase
import com.example.cafftrack.model.dao.CaffeineEntryDao

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "caffeine_tracker_db"
        ).build()
    }

    @Provides
    fun provideCaffeineEntryDao(db: AppDatabase): CaffeineEntryDao {
        return db.caffeineEntryDao()
    }
}