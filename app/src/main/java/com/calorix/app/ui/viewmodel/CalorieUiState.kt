package com.calorix.app.ui.viewmodel

import com.calorix.app.data.FoodEntry
import com.calorix.app.data.MealType

data class MealBreakdown(
    val breakfast: Int = 0,
    val lunch: Int = 0,
    val dinner: Int = 0,
    val snack: Int = 0
)

data class CalorieUiState(
    val dailyGoal: Int = 2000,
    val todayEntries: List<FoodEntry> = emptyList(),
    val allEntries: List<FoodEntry> = emptyList(),
    val todayTotalCalories: Int = 0,
    val isLoading: Boolean = false,
    val mealBreakdown: MealBreakdown = MealBreakdown(),
    val userFeedbackMessage: String? = null
) {
    val remainingCalories: Int
        get() = (dailyGoal - todayTotalCalories).coerceAtLeast(0)

    val progressFraction: Float
        get() = if (dailyGoal > 0) (todayTotalCalories.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f) else 0f

    val isOverGoal: Boolean
        get() = todayTotalCalories > dailyGoal

    val excessCalories: Int
        get() = if (isOverGoal) todayTotalCalories - dailyGoal else 0

    val groupedHistoryEntries: Map<String, List<FoodEntry>>
        get() = allEntries.groupBy { it.dateString }
}
