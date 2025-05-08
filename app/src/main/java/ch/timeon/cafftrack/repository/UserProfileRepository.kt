package ch.timeon.cafftrack.repository

import ch.timeon.cafftrack.model.dao.UserProfileDao
import ch.timeon.cafftrack.model.entity.UserProfileEntity
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