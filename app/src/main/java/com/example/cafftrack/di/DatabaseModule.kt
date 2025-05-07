package com.example.cafftrack.di

import android.content.Context
import androidx.room.Room
import com.example.cafftrack.model.AppDatabase
import com.example.cafftrack.model.dao.CaffeineEntryDao
import com.example.cafftrack.model.dao.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
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

    @Provides
    fun provideUserProfileDao(db: AppDatabase): UserProfileDao {
        return db.userProfileDao()
    }
}
