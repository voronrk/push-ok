package com.example.microplanner.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Тип длительности дела
enum class DurationType {
    SHORT,  // Быстрое (до 15 мин)
    MEDIUM  // Среднее (до 30 мин)
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: String, // UUID
    val title: String,
    val durationType: DurationType,
    val periodicityDays: Int, // 0 = без ограничений, 1, 2, 3, 7, 14, 30
    val isActive: Boolean = true,
    val isPredefined: Boolean, // true для стандартных дел, false для пользовательских
    val lastCompletedDate: Long? = null // Timestamp последнего успешного завершения (в миллисекундах)
)