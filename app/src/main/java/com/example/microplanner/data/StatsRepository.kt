package com.example.microplanner.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.random.Random

val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

data class AppStats(
    val totalCompleted: Int = 0,
    val totalSkipped: Int = 0,
    val currentStreak: Int = 0,
    val totalTimeReclaimedMinutes: Int = 0
)

class StatsRepository(private val context: Context) {
    val statsFlow: Flow<AppStats> = context.appDataStore.data.map { preferences ->
        AppStats(
            totalCompleted = preferences[TOTAL_COMPLETED] ?: 0,
            totalSkipped = preferences[TOTAL_SKIPPED] ?: 0,
            currentStreak = preferences[CURRENT_STREAK] ?: 0,
            totalTimeReclaimedMinutes = preferences[TOTAL_TIME_RECLAIMED] ?: 0
        )
    }

    suspend fun getOrAssignABGroup(): String {
        val currentGroup = context.appDataStore.data.first()[AB_GROUP]
        return if (currentGroup != null) {
            currentGroup
        } else {
            val newGroup = if (Random.nextBoolean()) "A" else "B"
            context.appDataStore.edit { it[AB_GROUP] = newGroup }
            newGroup
        }
    }

    suspend fun incrementCompleted(minutesSpent: Int) {
        context.appDataStore.edit { preferences ->
            preferences[TOTAL_COMPLETED] = (preferences[TOTAL_COMPLETED] ?: 0) + 1
            preferences[CURRENT_STREAK] = (preferences[CURRENT_STREAK] ?: 0) + 1
            preferences[TOTAL_TIME_RECLAIMED] = (preferences[TOTAL_TIME_RECLAIMED] ?: 0) + minutesSpent
        }
    }

    suspend fun incrementSkipped() {
        context.appDataStore.edit { preferences ->
            preferences[TOTAL_SKIPPED] = (preferences[TOTAL_SKIPPED] ?: 0) + 1
        }
    }

    suspend fun resetStreak() {
        context.appDataStore.edit { preferences ->
            preferences[CURRENT_STREAK] = 0
        }
    }

    companion object {
        val TOTAL_COMPLETED = intPreferencesKey("total_completed")
        val TOTAL_SKIPPED = intPreferencesKey("total_skipped")
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        val TOTAL_TIME_RECLAIMED = intPreferencesKey("total_time_reclaimed")
        val AB_GROUP = stringPreferencesKey("ab_group")
    }
}