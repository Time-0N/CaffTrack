package ch.timeon.cafftrack.model

import androidx.room.TypeConverter
import ch.timeon.cafftrack.model.enums.Sex

class Converters {

    @TypeConverter
    fun fromSex(value: Sex): String = value.name

    @TypeConverter
    fun toSex(value: String): Sex = Sex.valueOf(value)
}