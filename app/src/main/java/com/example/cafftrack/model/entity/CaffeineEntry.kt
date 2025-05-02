package com.example.cafftrack.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "caffeine_entries")
data class CaffeineEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val caffeineMg: Int,
    val timestamp: Long = System.currentTimeMillis()
)

enum class CaffeineType {
    DRINK,
    SUPPLEMENT,
    MEAL,
    OTHER
}