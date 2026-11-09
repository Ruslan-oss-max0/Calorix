package com.calorix.app.data

import kotlinx.coroutines.flow.Flow

class FoodRepository(
    private val foodDao: FoodDao,
    private val preferencesRepository: UserPreferencesRepository
) {
    val dailyGoalFlow: Flow<Int> = preferencesRepository.dailyCalorieGoal

    fun getEntriesForDate(dateString: String): Flow<List<FoodEntry>> {
        return foodDao.getEntriesForDate(dateString)
    }

    fun getAllEntries(): Flow<List<FoodEntry>> {
        return foodDao.getAllEntries()
    }

    fun getTotalCaloriesForDate(dateString: String): Flow<Int?> {
        return foodDao.getTotalCaloriesForDate(dateString)
    }

    suspend fun insertEntry(name: String, calories: Int, mealType: MealType): Long {
        val entry = FoodEntry(
            name = name.trim(),
            calories = calories,
            mealType = mealType
        )
        return foodDao.insertEntry(entry)
    }

    suspend fun deleteEntry(entry: FoodEntry) {
        foodDao.deleteEntry(entry)
    }

    suspend fun deleteById(id: Long) {
        foodDao.deleteById(id)
    }

    suspend fun setDailyCalorieGoal(goal: Int) {
        preferencesRepository.saveDailyCalorieGoal(goal)
    }

    suspend fun clearAll() {
        foodDao.clearAll()
    }
}
