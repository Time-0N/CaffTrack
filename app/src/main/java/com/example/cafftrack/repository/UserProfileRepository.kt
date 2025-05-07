package com.example.cafftrack.repository

import com.example.cafftrack.model.dao.UserProfileDao
import com.example.cafftrack.model.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val dao: UserProfileDao
) {
    fun getProfile(): Flow<UserProfileEntity?> = dao.getProfile()

    suspend fun insertOrUpdateProfile(profile: UserProfileEntity) = dao.insertOrUpdate(profile)
}