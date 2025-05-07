package com.example.cafftrack.model

import androidx.room.TypeConverter
import com.example.cafftrack.model.enums.Sex

class Converters {

    @TypeConverter
    fun fromSex(value: Sex): String = value.name

    @TypeConverter
    fun toSex(value: String): Sex = Sex.valueOf(value)
}