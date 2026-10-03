package com.example.microplanner.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DurationType {
    SHORT,
    MEDIUM
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: String,
    val title: String,
    val durationType: DurationType,
    val periodicityDays: Int,
    val isActive: Boolean = true,
    val isPredefined: Boolean,
    val lastCompletedDate: Long? = null,
    val lastSkippedDate: Long? = null
)