package com.calorix.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        val DAILY_CALORIE_GOAL_KEY = intPreferencesKey("daily_calorie_goal")
        const val DEFAULT_CALORIE_GOAL = 2000
    }

    val dailyCalorieGoal: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[DAILY_CALORIE_GOAL_KEY] ?: DEFAULT_CALORIE_GOAL
    }

    suspend fun saveDailyCalorieGoal(goal: Int) {
        context.dataStore.edit { preferences ->
            preferences[DAILY_CALORIE_GOAL_KEY] = goal
        }
    }
}
