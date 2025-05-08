package ch.timeon.cafftrack.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ch.timeon.cafftrack.model.enums.Sex

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val weightKg: Int,
    val sex: Sex
)