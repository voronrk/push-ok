package com.example.microplanner.data

import androidx.room.TypeConverter
import com.example.microplanner.domain.model.DurationType

class Converters {
    @TypeConverter
    fun fromDurationType(value: DurationType): String {
        return value.name
    }

    @TypeConverter
    fun toDurationType(value: String): DurationType {
        return DurationType.valueOf(value)
    }
}